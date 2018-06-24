package com.oneandone.ejbcdiunit.simulators.sftpserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.simulators.sftpclient.SftpConnector;
import com.oneandone.ejbcdiunit.simulators.sftpclient.TestSftpConfiguration;
import com.oneandone.ejbcdiunit.simulators.sftpclient.upload.UploadFileStrategy;


@RunWith(EjbUnitRunner.class)
@AdditionalClasses({})
public class SftpConnectorCdiTest {

    private static final String HOST = "localhost";
    private static final Integer PORT = 12322;
    private static final String USER_NAME = "ejbcdiunituser";
    private static final String PASSWORD = "ejbcdiunitpassword";

    private static final String PATH_IN = "in";
    private static final String PATH_IN_TEMP = "in/temp";
    private static final String PATH_OUT = "out";
    private static final String PATH_OUT_TEMP = "out/temp";
    private static final String PATH_ARCHIVE = "archive";

    private static final Integer CONNECTION_RETRY_SLEEP = 3000;
    private static final Integer CONNECTION_RETRIES = 3;
    private static final Integer CONNECTION_TIMEOUT = 60000;

    private static final String STRICT_HOST_KEY_SETTING = "no";

    private static final String FILE_CONTENT_STRING = "UNA[...]UNZ";
    private static final byte[] FILE_CONTENT;
    private static final String EXCEPTION_MESSAGE_NO_AUTH = "Auth fail";
    private static final SftpServer SFTP_SERVER = new SftpServer(PORT, USER_NAME, PASSWORD, null);

    static {
        try {
            FILE_CONTENT = FILE_CONTENT_STRING.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String randomFileName = null;

    @Inject
    private SftpConnector sftpConnectorTempDir;

    @Inject
    private SftpConnector sftpConnectorTmpFile;

    @Inject
    private SftpConnector sftpConnectorNoRename;

    @Inject
    private SftpConnector sftpConnectorTmpPrefix;

    @Inject
    private SftpConnector sftpConnectorInvalid;

    @Inject
    private SftpConnector sftpConnectorNoAuth;

    @BeforeClass
    public static void beforeClass() {}

    @BeforeClass
    public static void startServer() throws IOException {
        SFTP_SERVER.start();
        SFTP_SERVER.makeDirectory(PATH_IN);
        SFTP_SERVER.makeDirectory(PATH_OUT);
        SFTP_SERVER.makeDirectory(PATH_IN_TEMP);
        SFTP_SERVER.makeDirectory(PATH_ARCHIVE);
        SFTP_SERVER.makeDirectory(PATH_OUT_TEMP);
    }

    @AfterClass
    public static void stopServer() throws IOException {
        SFTP_SERVER.stop();
    }

    @PostConstruct
    public void initSftpServer() {
        sftpConnectorTempDir.init(
                new TestSftpConfiguration(HOST,
                        PORT,
                        USER_NAME,
                        PASSWORD,
                        CONNECTION_RETRY_SLEEP,
                        CONNECTION_RETRIES,
                        CONNECTION_TIMEOUT, STRICT_HOST_KEY_SETTING,
                        null, null, null, null, null, 1, UploadFileStrategy.TempDirectory.getConfigValue(), 1, 1, 0, false));

        sftpConnectorTmpFile.init(
                new TestSftpConfiguration(HOST,
                        PORT,
                        USER_NAME,
                        PASSWORD,
                        CONNECTION_RETRY_SLEEP,
                        CONNECTION_RETRIES,
                        CONNECTION_TIMEOUT, STRICT_HOST_KEY_SETTING,
                        null, null, null, null, null, 1, UploadFileStrategy.TmpFileExtension.getConfigValue(), 1, 1, 0, false));

        sftpConnectorNoRename.init(
                new TestSftpConfiguration(HOST,
                        PORT,
                        USER_NAME,
                        PASSWORD,
                        CONNECTION_RETRY_SLEEP,
                        CONNECTION_RETRIES,
                        CONNECTION_TIMEOUT, STRICT_HOST_KEY_SETTING,
                        null, null, null, null, null, 1, UploadFileStrategy.NoTempFile.getConfigValue(), 1, 1, 0, false));

        sftpConnectorTmpPrefix.init(
                new TestSftpConfiguration(HOST,
                        PORT,
                        USER_NAME,
                        PASSWORD,
                        CONNECTION_RETRY_SLEEP,
                        CONNECTION_RETRIES,
                        CONNECTION_TIMEOUT, STRICT_HOST_KEY_SETTING,
                        null, null, null, null, null, 1, UploadFileStrategy.TmpFilePrefix.getConfigValue(), 1, 1, 0, false));


        sftpConnectorInvalid.init(
                new TestSftpConfiguration("invalid_host_name",
                        PORT,
                        USER_NAME,
                        PASSWORD,
                        CONNECTION_RETRY_SLEEP,
                        CONNECTION_RETRIES,
                        CONNECTION_TIMEOUT, STRICT_HOST_KEY_SETTING,
                        null, null, null, null, null, 1, UploadFileStrategy.TempDirectory.getConfigValue(), 1, 1, 0, false));

        sftpConnectorNoAuth.init(
                new TestSftpConfiguration(HOST,
                        PORT,
                        USER_NAME,
                        PASSWORD + "wrong",
                        CONNECTION_RETRY_SLEEP,
                        CONNECTION_RETRIES,
                        CONNECTION_TIMEOUT, STRICT_HOST_KEY_SETTING,
                        null, null, null, null, null, 1, UploadFileStrategy.TempDirectory.getConfigValue(), 1, 1, 0, false));
    }

    @Before
    public void beforeTest() {
        final Random generator = new Random();
        final Integer rand = generator.nextInt(999);
        randomFileName = "REQ1and1" + System.nanoTime() + rand + ".CAS";
    }

    @After
    public void afterTest() {
        // cleanUpServer();
    }

    /**
     * Test uploading files for all possible {@link UploadFileStrategy}s
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testUploadFile() throws UnsupportedEncodingException {
        /*
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
        uploadFile(sftpConnectorTempDir);
        uploadFile(sftpConnectorTmpFile);
        uploadFile(sftpConnectorNoRename);
        uploadFile(sftpConnectorTmpPrefix);
    }

    private void uploadFile(SftpConnector sftp) throws UnsupportedEncodingException {
        beforeTest();
        sftp.uploadFile(FILE_CONTENT, PATH_OUT, randomFileName);
        assertTrue(sftp.fileExists(PATH_OUT, randomFileName));

        final byte[] fileConent = sftp.getFileContent(PATH_OUT, randomFileName);
        assertEquals(new String(FILE_CONTENT, "UTF-8"), new String(fileConent, "UTF-8"));
    }

    @Test(expected = RuntimeException.class)
    public void testUploadFileToInvalidServer() {
        sftpConnectorInvalid.uploadFile(FILE_CONTENT, PATH_OUT, randomFileName);
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteNonExistingFile() {
        assertFalse(sftpConnectorTempDir.fileExists(PATH_OUT, randomFileName));
        sftpConnectorTempDir.deleteFile(PATH_IN, randomFileName);
    }

    @Test
    public void testNoAuth() {
        try {
            assertFalse(sftpConnectorNoAuth.fileExists(PATH_OUT, randomFileName));
            Assert.fail();
        } catch (RuntimeException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String message = sw.toString(); // stack trace as a string
            assertTrue(message.contains(EXCEPTION_MESSAGE_NO_AUTH));
        }
    }

    @Test
    public void testMoveFile() {
        sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, randomFileName);
        assertTrue(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName));

        sftpConnectorTempDir.moveFile(PATH_IN, PATH_ARCHIVE, randomFileName);
        assertTrue(sftpConnectorTempDir.fileExists(PATH_ARCHIVE, randomFileName));
        assertFalse(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName));
    }

    @Test(expected = RuntimeException.class)
    public void testMoveNonExistingFile() {
        assertFalse(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName));
        sftpConnectorTempDir.moveFile(PATH_IN, PATH_ARCHIVE, randomFileName);
    }

    @Test
    public void testDeleteFile() {
        sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, randomFileName);
        assertTrue(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName));

        sftpConnectorTempDir.deleteFile(PATH_IN, randomFileName);
        assertFalse(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName));
    }

    @Test
    public void testDeleteFiles() {
        sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, randomFileName);
        sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, randomFileName + "1");
        assertTrue(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName));
        assertTrue(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName + "1"));

        sftpConnectorTempDir.deleteFiles(PATH_IN, Arrays.asList(randomFileName, randomFileName + "1"));
        assertFalse(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName));
        assertFalse(sftpConnectorTempDir.fileExists(PATH_IN, randomFileName + "1"));
    }

    @Test
    public void testGetFileNamesFromFolder() {
        sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_OUT, randomFileName);

        final List<String> fileNames = sftpConnectorTempDir.getFileNamesFromPath(PATH_OUT);
        assertTrue(fileNames.contains(randomFileName));
    }

    @Test
    public void testDownloadFile() throws Exception {
        // prepare
        sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, randomFileName);

        // test
        Path source = Paths.get(".", PATH_IN, randomFileName);
        Path destination = Paths.get(".", "target", randomFileName);
        sftpConnectorTempDir.downloadFile(source, destination);

        // verify
        String actualFileContent = new String(Files.readAllBytes(destination), "UTF-8");
        assertEquals(new String(FILE_CONTENT, "UTF-8"), actualFileContent);
    }

    @Test
    public void testDownloadFileToTemp() throws IOException {
        // prepare
        sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, randomFileName);

        // test
        Path source = Paths.get(".", PATH_IN, randomFileName);
        Path destination = Paths.get(File.createTempFile(randomFileName, "").toURI());
        sftpConnectorTempDir.downloadFile(source, destination);

        // verify
        String actualFileContent = new String(Files.readAllBytes(destination), "UTF-8");
        assertEquals(new String(FILE_CONTENT, "UTF-8"), actualFileContent);
    }

    /**
     * @see <a href="https://dev-jira.1and1.org/browse/MOPI-1968">MOPI-1968</a>
     * @throws IOException
     */
    @Test
    public void testDownloadFileToTempPermissionDenied() throws IOException {
        // prepare
        sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, randomFileName);

        // test
        Path source = Mockito.mock(Path.class);
        // this is at the same place (within the try) where the original exception would have been thrown.
        // Is a workaround as we do not have access to the channel created within the sftpConnectorTempDir
        final RuntimeException expectedException =
                new RuntimeException("wrapper for method signature purposes when mocking", new SftpException(
                        ChannelSftp.SSH_FX_PERMISSION_DENIED, "Permission denied"));
        Mockito.when(source.toString()).thenThrow(expectedException);

        Path destination = Paths.get(File.createTempFile(randomFileName, "").toURI());
        try {
            sftpConnectorTempDir.downloadFile(source, destination);
            fail("Exception shoud be thrown");
        } catch (Exception ex) {
            assertSame(expectedException, ex);
        }

        // verify
        assertFalse("File should not be created on error", destination.toFile().exists());
    }

    /**
     * Expects 2 retries.
     */
    @Test(expected = RuntimeException.class)
    public void testRetriesForInvalidHost() {
        sftpConnectorInvalid.uploadFile(FILE_CONTENT, PATH_OUT, randomFileName);
    }

    /**
     * Expects no retries.
     */
    @Test
    public void testRetriesForInvalidPassword() {
        try {
            sftpConnectorNoAuth.uploadFile(FILE_CONTENT, PATH_OUT, randomFileName);
            fail("exception expected");
        } catch (RuntimeException ex) {
            assertEquals("unable to connect to sftp-server.", ex.getMessage());
        }
    }

    /*
     * @Test public void testGetFileNamesFromPath_VodafonePattern() { TransactionId<TransactionIdVodafone> id = uploadFilesForPatternTestVodafone();
     * List<String> fileNamesFromPath = sftpConnectorTempDir.getFileNamesFromPath(PATH_IN,
     * TransactionIdFactoryVodafone.INCOMING_EXCHANGE_FILE_NAME_PATTERN_VODAFONE); assertThat(fileNamesFromPath, hasSize(2));
     * assertThat(fileNamesFromPath, containsInAnyOrder(id.getIncomingFileName(), "12345v11.rsp")); } private TransactionId<TransactionIdVodafone>
     * uploadFilesForPatternTestVodafone() { TransactionId<TransactionIdVodafone> idVodafone = new TransactionIdFactoryVodafone().fromJobInfo(1604L,
     * 86); sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, idVodafone.getIncomingFileName()); sftpConnectorTempDir.uploadFile(FILE_CONTENT,
     * PATH_IN, "this_should_not_work.txt" + randomFileName); sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, "RSPEINS12345_11.CAS");
     * sftpConnectorTempDir.uploadFile(FILE_CONTENT, PATH_IN, "12345v11.rsp"); return idVodafone; }
     */

    private void cleanUpServer() {
        List<String> fileNamesIn = sftpConnectorTempDir.getFileNamesFromPath(PATH_IN);
        sftpConnectorTempDir.deleteFiles(PATH_IN, fileNamesIn);
    }
}
