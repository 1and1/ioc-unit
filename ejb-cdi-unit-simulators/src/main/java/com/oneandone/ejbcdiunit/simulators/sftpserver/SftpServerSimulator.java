package com.oneandone.ejbcdiunit.simulators.sftpserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * @author aschoerk
 */
public class SftpServerSimulator {

    private static final int FIRST_NON_STANDARD_IP_PORT = 49152;
    private static final int MAX_PORT_SEARCH_TRIES = 100;
    private static final String SFTP_SIMULATOR_PRIVATE_KEY_FILE = "ftp_authentication_private_key_no_password.pem";
    private static final String SFTP_SIMULATOR_PUBLIC_KEY_FILE = "ftp_authentication_public_key.pem";
    private static final String INTERNAL_SFTP_SIMULATOR_PUBLIC_KEY_PATH = "keys/" + SFTP_SIMULATOR_PUBLIC_KEY_FILE;
    private static final String INTERNAL_SFTP_SIMULATOR_PRIVATE_KEY_PATH = "keys/" + SFTP_SIMULATOR_PRIVATE_KEY_FILE;
    private static String tmpPublicKey;
    private static transient SftpServer sftpServer = null;
    private static transient int startCount = 0;
    private static boolean startAtOriginalPort = true;
    public int port;
    @Inject
    private Logger logger;
    private String sftpServerSimulatorKeyFileRefreshInterval = "10000";

    /**
     * initialize SSH Key FTP authentication support by copying private & public key to temp folder. The path is referenced by property files.
     */
    private void initKeys() {
        try (InputStream publicKeyStream = SftpServerSimulator.class.getClassLoader().getResourceAsStream(INTERNAL_SFTP_SIMULATOR_PUBLIC_KEY_PATH);
                InputStream privateKeyStream =
                        SftpServerSimulator.class.getClassLoader().getResourceAsStream(INTERNAL_SFTP_SIMULATOR_PRIVATE_KEY_PATH)) {
            // copy public & private key to temp folder
            String systemTmpDir = System.getProperty("java.io.tmpdir");
            Path tempPath = Paths.get(systemTmpDir + "/keys"); // must match to _default.properties entries of private key path
            Path tmpPublicKeyPath = tempPath.resolve(SFTP_SIMULATOR_PUBLIC_KEY_FILE);
            Path tmpPrivateKeyPath = tempPath.resolve(SFTP_SIMULATOR_PRIVATE_KEY_FILE);
            tmpPublicKey = tmpPublicKeyPath.toString();

            // create subfolder
            Files.createDirectories(tempPath);

            // copy files
            if (!tmpPrivateKeyPath.toFile().exists()) {
                Files.copy(privateKeyStream, tmpPrivateKeyPath);
            }
            if (!tmpPublicKeyPath.toFile().exists()) {
                Files.copy(publicKeyStream, tmpPublicKeyPath);
            }
        } catch (DirectoryNotEmptyException | FileAlreadyExistsException ex) {
            logger.warn("key files already exists, ignore exception: " + ex.getMessage());
        } catch (IOException ex) {
            logger.error("cannot copy keyfiles, unknown exception: ", ex);
        }
    }

    /**
     * Try to start the sftp-server.
     */
    @PostConstruct
    public void start() {

        if (sftpServer != null) {
            startCount++;
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    initKeys();

                    try {
                        Thread.sleep(Long.parseLong(sftpServerSimulatorKeyFileRefreshInterval));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, "SftpServerSimulator" + startCount).start();


        try {
            initKeys();

            int count = 0;
            port = FIRST_NON_STANDARD_IP_PORT;
            do {
                try (ServerSocket serverSocket = new ServerSocket(0)) {
                    port = serverSocket.getLocalPort();
                }
                sftpServer.setPort(port);
                try {
                    sftpServer.start();
                    break;
                } catch (Exception e) {
                    if (count++ > MAX_PORT_SEARCH_TRIES) {
                        throw new IOException("Could not create SftpServerSimulator after 100 tries", e);
                    }
                }
            } while (true);
            logger.info("started sftp-server on port: " + port);
        } catch (IOException e) {
            logger.error("error while starting sftp-server", e);
        }
    }

    /**
     * Try to stop the sftp-server.
     */
    @PreDestroy
    public void stop() {
        if (startCount > 0) {
            startCount--;
            return;
        }

        logger.info("stop sftp-server on port: " + port);

        try {
            sftpServer.stop();
            sftpServer = null;
        } catch (IOException e) {
            logger.error("error while stopping sftp-server", e);
        }
    }
}
