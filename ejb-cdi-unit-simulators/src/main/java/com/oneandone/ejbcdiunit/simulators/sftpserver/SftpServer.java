package com.oneandone.ejbcdiunit.simulators.sftpserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.sshd.common.FactoryManager;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.ServerFactoryManager;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SftpServer.
 *
 * @author nkaiser
 */
public class SftpServer {

    private static final String MAX_CONCURRENT_SFTP_SERVER_SIMULATOR_SESSIONS = "200";
    private static final String MAX_SFTP_SERVER_IDLE_SESSION_TIMEOUT = "10000";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String user;
    private final String password;
    private final String pathFilePublicKeyInPemFormat;
    private final Path tmpBaseDir;
    private Integer port;
    private List<RSAPublicKey> allowedPublicKeys = new ArrayList<>();
    private SshServer sshServer = null;

    /**
     * Constructor.
     *
     * @param port
     *            port that should be used
     * @param user
     *            user that is authorized to access server
     * @param password
     *            password for authorization
     * @param pathFilePublicKeyInPemFormat
     *            null or a file with public key that the the server should accept for key file authentication
     */
    public SftpServer(final Integer port, final String user, final String password,
            final String pathFilePublicKeyInPemFormat) {
        this.port = port;
        this.user = user;
        this.password = password;
        this.pathFilePublicKeyInPemFormat = pathFilePublicKeyInPemFormat;

        try {
            tmpBaseDir = Files.createTempDirectory("ejbcdiunit_sftp");
            log.info("Temporary directoy created: " + tmpBaseDir);
            tmpBaseDir.toFile().deleteOnExit();
        } catch (final IOException e) {
            throw new RuntimeException("failed to create tempoary directory for SFTP server!", e);
        }
    }

    private static RSAPublicKey getRsaPublicKey(String publicKeyPemFileName) throws IOException, GeneralSecurityException {
        Path publickKeyPemPath = Paths.get(publicKeyPemFileName);
        byte[] publicKeyPemBytes = Files.readAllBytes(publickKeyPemPath);
        String publicKeyPemString = new String(publicKeyPemBytes);

        publicKeyPemString = publicKeyPemString.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPemString = publicKeyPemString.replace("-----END PUBLIC KEY-----", "");
        byte[] publicKeyPemDecoded = DatatypeConverter.parseBase64Binary(publicKeyPemString);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyPemDecoded);
        KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");

        PublicKey publicKey = rsaKeyFactory.generatePublic(keySpec);
        return (RSAPublicKey) publicKey;
    }

    public Path getTmpBaseDir() {
        return tmpBaseDir;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public SshServer getSshServer() {
        return sshServer;
    }

    /**
     * Sets up a server on the fly.
     *
     * @throws IOException
     *             if an exception occurs while creating a file
     */
    public void start() throws IOException {
        sshServer = SshServer.setUpDefaultServer();
        sshServer.setPort(port);

        sshServer.getProperties().put(ServerFactoryManager.MAX_CONCURRENT_SESSIONS, MAX_CONCURRENT_SFTP_SERVER_SIMULATOR_SESSIONS);
        sshServer.getProperties().put(FactoryManager.IDLE_TIMEOUT, MAX_SFTP_SERVER_IDLE_SESSION_TIMEOUT);
        allowedPublicKeys = parsePublicFile(pathFilePublicKeyInPemFormat);

        boolean authenticationFound = false;

        // private key: transport security
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

        // Simple password authentication.
        if (null != password && !password.isEmpty()) {
            log.trace("Password authentification defined");
            sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
                @Override
                public boolean authenticate(String username, String pwd, ServerSession session) throws PasswordChangeRequiredException {
                    return null != user && user.equals(username) && password.equals(pwd);
                }


            });
            authenticationFound = true;
        }

        // Key file authentication.
        if (!allowedPublicKeys.isEmpty()) {
            log.trace("Public Key authentification defined: " + allowedPublicKeys);
            sshServer.setPublickeyAuthenticator(
                    new PublickeyAuthenticator() {
                        @Override
                        public boolean authenticate(String username, PublicKey publicKey, ServerSession serverSession) {
                            log.trace("Public key comming in to check: " + publicKey);
                            boolean precheck = null != user && user.equals(username) && null != publicKey;
                            // Simulator only supports RSA keys!
                            if (!precheck || !(publicKey instanceof RSAPublicKey)) {
                                log.debug("Not authentificated, precheck failed");
                                return false;
                            }
                            RSAPublicKey rsapublicKey = (RSAPublicKey) publicKey;
                            for (RSAPublicKey key : allowedPublicKeys) {
                                if (key.getPublicExponent().equals(rsapublicKey.getPublicExponent())
                                        && key.getModulus().equals(rsapublicKey.getModulus())) {
                                    log.trace("Matching authentification, check sucessful");
                                    return true;
                                }
                            }
                            log.debug("Not authentificated, no matching key found");
                            return false;
                        }
                    });
            authenticationFound = true;
        }

        if (!authenticationFound) {
            log.debug("no authentication method");
            throw new RuntimeException("Sftp: no password and no public key configurated");
        }

        List<NamedFactory<Command>> subsystemFactories = new ArrayList<>();
        subsystemFactories.add(new DelayedSftpSubsystemFactory());
        sshServer.setSubsystemFactories(subsystemFactories);

        VirtualFileSystemFactory fileSystemFactory = new VirtualFileSystemFactory();
        fileSystemFactory.setUserHomeDir(user, tmpBaseDir);
        sshServer.setFileSystemFactory(fileSystemFactory);

        sshServer.start();
    }

    private List<RSAPublicKey> parsePublicFile(final String publicKeyFileName) {
        ArrayList<RSAPublicKey> result = new ArrayList<>();
        if (null != publicKeyFileName && !publicKeyFileName.isEmpty()) {
            // Simulator only supports RSA keys!
            // step one: try pem format
            try {
                result.add(getRsaPublicKey(publicKeyFileName));
            } catch (Exception e) {
                log.debug("Can not parse", e);
            }

            // step two: try ssh-keygen format
            if (result.isEmpty()) {
                AuthorizedKeyDecoder decoder = new AuthorizedKeyDecoder();
                File file = new File(publicKeyFileName);
                try (Scanner scanner = new Scanner(file, "UTF-8")) {
                    scanner.useDelimiter("\n");
                    while (scanner.hasNext()) {
                        PublicKey k = decoder.decodePublicKey(scanner.next());
                        if (k instanceof RSAPublicKey) {
                            result.add((RSAPublicKey) k);
                        }
                    }
                } catch (Exception e) {
                    log.debug("Can not parse", e);
                }
            }
        }
        return result;
    }

    /**
     * Stops the server.
     *
     * @throws IOException
     *             if any i/o problems
     */
    public void stop() throws IOException {
        sshServer.stop(true);
        FileUtils.deleteDirectory(tmpBaseDir.toFile());
    }

    /**
     * Makes a new directory in the SFTP root.
     *
     * @param directoryName
     *            the name of the directory
     */
    public void makeDirectory(final String directoryName) {
        tmpBaseDir.resolve(directoryName).toFile().mkdirs();
    }

}
