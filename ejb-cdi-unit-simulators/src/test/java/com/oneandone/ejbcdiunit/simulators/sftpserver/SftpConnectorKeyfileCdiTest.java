package com.oneandone.ejbcdiunit.simulators.sftpserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.simulators.sftpclient.SftpConfiguration;
import com.oneandone.ejbcdiunit.simulators.sftpclient.SftpConnector;
import com.oneandone.ejbcdiunit.simulators.sftpclient.TestSftpConfiguration;
import com.oneandone.ejbcdiunit.simulators.sftpclient.upload.UploadFileStrategy;

@RunWith(EjbUnitRunner.class)
@AdditionalClasses({})
public class SftpConnectorKeyfileCdiTest {

    static final Pattern DEFAULT_NAME_PATTERN = Pattern.compile(".*");
    private static final String PRIVATE_KEY_NO_PASSWORD_PEM = "src/main/resources/keys/ftp_authentication_private_key_no_password.pem";
    private static final String PRIVATE_KEY_WITH_PASSWORD_PEM = "src/test/resources/keys/ftp_authentication_private_key_with_password.pem";
    private static final String PUBLIC_KEY_PEM = "src/main/resources/keys/ftp_authentication_public_key.pem";
    private static final String PRIVATE_KEY_NO_PASSWORD_SSHGEN = "src/test/resources/keys/ftp_authentication_keygen";
    private static final String PUBLIC_KEY_SSHGEN = "src/test/resources/keys/ftp_authentication_keygen.pub";
    private static final String HOST = "localhost";
    private static final Integer PORT = 12322;
    private static final String USER_NAME = "ejbcdiunituser";
    private static final String PASSWORD = "12345";
    private static final String PATH = "mypath";
    private static final Integer CONNECTION_RETRY_SLEEP = 3000;
    private static final Integer CONNECTION_RETRIES = 3;
    private static final Integer CONNECTION_TIMEOUT = 60000;
    private static final String STRICT_HOST_KEY_SETTING = "no";

    @Inject
    private SftpConnector sftpConnectorWithNoPassword;

    @Inject
    private SftpConnector sftpConnectorWithPassword;

    @Inject
    private SftpConnector sftpConnectorWithWrongPassword;

    @Inject
    private SftpConnector sftpConnectorWithKeyAndNoPassword;

    private static SftpConfiguration createSftpConfiguration(String password, String privateKeyPath) {
        return new TestSftpConfiguration(
                HOST,
                PORT,
                USER_NAME,
                password,
                CONNECTION_RETRY_SLEEP,
                CONNECTION_RETRIES,
                CONNECTION_TIMEOUT,
                STRICT_HOST_KEY_SETTING,
                privateKeyPath,
                DEFAULT_NAME_PATTERN,
                PATH,
                null,
                null,
                0,
                UploadFileStrategy.NoTempFile.getConfigValue(),
                0,
                0,
                0,
                false);
    }

    @Before
    public void initSftpConnectors() {
        sftpConnectorWithPassword.init(createSftpConfiguration(PASSWORD, PRIVATE_KEY_WITH_PASSWORD_PEM));
        sftpConnectorWithNoPassword.init(createSftpConfiguration("", PRIVATE_KEY_NO_PASSWORD_PEM));
        sftpConnectorWithWrongPassword.init(createSftpConfiguration("this_password_is_wrong", PRIVATE_KEY_WITH_PASSWORD_PEM));
        sftpConnectorWithKeyAndNoPassword.init(createSftpConfiguration("", PRIVATE_KEY_NO_PASSWORD_SSHGEN));
    }

    @Test
    public void testKeyfileAndPassword() throws Exception {
        SftpServer sftpServer = new SftpServer(PORT, USER_NAME, PASSWORD, PUBLIC_KEY_PEM);
        try {
            sftpServer.start();
            sftpServer.makeDirectory(PATH);
            assertEquals(0, sftpConnectorWithNoPassword.getFileNamesFromPath(PATH).size());
        } finally {
            sftpServer.stop();
        }
    }

    @Test
    public void testKeyfileOnlyNoPassword() throws Exception {
        SftpServer sftpServer = new SftpServer(PORT, USER_NAME, null, PUBLIC_KEY_PEM);
        try {
            sftpServer.start();
            sftpServer.makeDirectory(PATH);
            assertEquals(0, sftpConnectorWithNoPassword.getFileNamesFromPath(PATH).size());
        } finally {
            sftpServer.stop();
        }
    }

    @Ignore  // don't want to expect cloners to have JCE installed
    @Test
    // test needs "Java Cryptography Extension (JCE) Unlimited Strength" to work correctly
    public void testKeyfileOnlyWithPassword() throws Exception {
        SftpServer sftpServer = new SftpServer(PORT, USER_NAME, null, PUBLIC_KEY_PEM);
        try {
            sftpServer.start();
            sftpServer.makeDirectory(PATH);
            assertEquals(0, sftpConnectorWithPassword.getFileNamesFromPath(PATH).size());
        } finally {
            sftpServer.stop();
        }
    }

    @Test
    public void testNoAuthenticationDefined() throws Exception {
        SftpServer sftpServer = new SftpServer(PORT, USER_NAME, null, null);
        try {
            sftpServer.start();
            fail("expected exception");
        } catch (Exception e) {
            assertEquals("Sftp: no password and no public key configurated", e.getMessage());
        } finally {
            sftpServer.stop();
        }
    }

    @Test
    public void testKeyfileWrongPassword() throws Exception {
        SftpServer sftpServer = new SftpServer(PORT, USER_NAME, null, PUBLIC_KEY_PEM);
        sftpServer.start();
        sftpServer.makeDirectory(PATH);
        try {
            sftpConnectorWithWrongPassword.getFileNamesFromPath(PATH);
            fail("expected exception");
        } catch (Exception e) {
            assertEquals("failed to connect to sftp-server.", e.getMessage());
        } finally {
            sftpServer.stop();
        }
    }

    @Test
    public void testKeygenfileOnlyNoPassword() throws Exception {
        SftpServer sftpServer = new SftpServer(PORT, USER_NAME, null, PUBLIC_KEY_SSHGEN);
        try {
            sftpServer.start();
            sftpServer.makeDirectory(PATH);
            assertEquals(0, sftpConnectorWithKeyAndNoPassword.getFileNamesFromPath(PATH).size());
        } finally {
            sftpServer.stop();
        }
    }

}
