package com.oneandone.ejbcdiunit.simulators.sftpclient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.oneandone.ejbcdiunit.simulators.sftpclient.upload.UploadFileHandler;
import com.oneandone.ejbcdiunit.simulators.sftpclient.upload.UploadFileHandlerFactory;

/**
 * SftpConnector connects to an sftp-server.<br>
 * Supports methods to upload, read and delete files on an sftp-server. <br>
 * Each public method is synchronized but shall not be used in multiple threads
 *
 * @author nkaiser
 */
public class SftpConnector {


    private static final long FACTOR_TO_SECONDS = 1000L;
    private final Logger log = LoggerFactory.getLogger(SftpConnector.class);
    protected SftpConfiguration sftpConfiguration;
    @Inject
    SftpConnectionFactory sftpConnectionFactory;
    private SftpConnection sftpConnection = null;
    private UploadFileHandler uploadFileHandler;

    /**
     * Automatic disconnect
     */
    @PreDestroy
    public void preDestroy() {
        disconnect();
    }

    /**
     * Initializer method.
     *
     * @param sftpConfiguration
     *            configuration values
     */
    public void init(SftpConfiguration sftpConfiguration) {
        this.sftpConfiguration = sftpConfiguration;
        disconnect();
        logSftpConfiguration();
        createUploadFileHandler();
    }

    /**
     * output the current SftpConfiguration to log
     */
    private void logSftpConfiguration() {
        log.debug("host: {}", sftpConfiguration.getHost());
        log.debug("port: {}", sftpConfiguration.getPort());
        log.trace("userName: {}", sftpConfiguration.getUsername());
        log.trace("connectionRetrySleep: {}", sftpConfiguration.getRetrySleep());
        log.trace("connectionRetries: {}", sftpConfiguration.getRetries());
        log.trace("connectionTimeout: {}", sftpConfiguration.getTimeout());
        log.trace("strictHostKeyChecking: {}", sftpConfiguration.getStrictHostKeySetting());
        log.trace("pathOut: {}", sftpConfiguration.getPath());
        log.trace("startTime: {}", sftpConfiguration.getStartTime());
        log.trace("endTime: {}", sftpConfiguration.getEndTime());
        log.trace("priority: {}", sftpConfiguration.getPriority());
        log.trace("privateKeyPath: {}", sftpConfiguration.getPrivateKeyPath());
        log.trace("UploadFileStrategy: {}", sftpConfiguration.getUploadFileStrategy());
    }

    /**
     * create {@link UploadFileHandler} depending on {@link UploadFileStrategy}
     */
    private void createUploadFileHandler() {
        UploadFileHandlerFactory uploadFileHandlerFactory = new UploadFileHandlerFactory();
        uploadFileHandler = uploadFileHandlerFactory.getUploadFileHandler(sftpConfiguration.getUploadFileStrategy());
    }

    /**
     * Uploads a file to the sftp-server in default directory.
     *
     * @param fileContent
     *            content of the file (example: asd123qwefffxyc)
     * @param fileName
     *            name of the file on the server (example: REQ1and1654300.CAS)
     */
    public void uploadFile(final byte[] fileContent, final String fileName) {
        uploadFile(fileContent, sftpConfiguration.getPath(), fileName);
    }

    /**
     * Uploads a file to the sftp-server.
     *
     * @param fileContent
     *            content of the file (example: asd123qwefffxyc)
     * @param path
     *            relative target-path on the sftp-server (example: out/)
     * @param fileName
     *            name of the file on the server (example: REQ1and1654300.CAS)
     */
    public void uploadFile(final byte[] fileContent, final String path, final String fileName) {
        log.info(sftpConfiguration.getClass().getSimpleName() + " uploading with hystrix: " + String.valueOf(sftpConfiguration.isHystrixEnabled()));

        innerUploadFile(fileContent, path, fileName);

    }

    protected synchronized void innerUploadFile(final byte[] fileContent, final String path, final String fileName) {
        log.info("call innerUploadFile of class: " + this.getClass().getSimpleName());
        final long startMillis = Instant.now().toEpochMilli();
        final String uploadFile;
        uploadFile = uploadFileHandler.getUploadFileName(path, fileName);
        final String targetFile = Paths.get(".", path, fileName).toString();
        boolean isUploadSuccessful = false;
        connect();
        try (InputStream inputStream = new ByteArrayInputStream(fileContent)) {
            log.trace("uploadFile = {}", uploadFile);
            log.trace("targetFile = {}", targetFile);
            sftpConnection.getChannel().put(inputStream, uploadFile);
            isUploadSuccessful = true;
            uploadFileHandler.doRenameToTargetFile(sftpConnection.getChannel(), uploadFile, targetFile);

            log.info("file is uploaded as {} and renamed to {} in {} ms", uploadFile, targetFile,
                    (Instant.now().toEpochMilli() - startMillis));
        } catch (Exception e) {
            String errMsg = uploadFileHandler.doExceptionHandling(sftpConnection.getChannel(), uploadFile, targetFile, isUploadSuccessful);
            errMsg = errMsg + ", file content length = " + fileContent.length;
            if (e instanceof SftpException) {
                errMsg = errMsg + ", " + getSftpExceptionErrorId((SftpException) e);
            }
            throw new RuntimeException(errMsg, e);
        } finally {
            disconnect();
        }
    }

    /**
     * Deletes a file from sftp-server.
     *
     * @param path
     *            relative target-path on the sftp-server (example: in/)
     * @param fileName
     *            name of the file on the server (example: RSP1and16543000.CAS)
     * @throws RuntimeException
     */
    public synchronized void deleteFile(final String path, final String fileName) {
        connect();
        try {
            final String targetFile = Paths.get(".", path, fileName).toString();
            sftpConnection.getChannel().rm(targetFile);
            log.info("file is deleted: {}", targetFile);
        } catch (final SftpException e) {
            throw new RuntimeException("SftpException while deleting file " + fileName + " in path " + path, e);
        } finally {
            disconnect();
        }
    }

    /**
     * Deletes a file from sftp-server.
     *
     * @param path
     *            relative target-path on the sftp-server (example: in/)
     * @param fileNames
     *            name of the file on the server (example: RSP1and16543000.CAS)
     * @throws RuntimeException
     */
    public synchronized void deleteFiles(final String path, final List<String> fileNames) {
        connect();
        String fileName = null;
        Iterator<String> it = fileNames.iterator();
        try {
            while (it.hasNext()) {
                fileName = it.next();
                final String targetFile = Paths.get(".", path, fileName).toString();
                sftpConnection.getChannel().rm(targetFile);
                log.info("file is deleted: {}", targetFile);
            }
        } catch (final SftpException e) {
            throw new RuntimeException("SftpException while deleting file " + fileName + " in path " + path, e);
        } finally {
            disconnect();
        }
    }

    /**
     * Moves a file to another path on the SFTP server.
     *
     * @param oldPath
     *            the current path of the file
     * @param newPath
     *            the destination path to move the file to
     * @param fileName
     *            the file to move
     */
    public synchronized void moveFile(final String oldPath, final String newPath, final String fileName) {
        connect();
        try {
            final String newFile = Paths.get(".", newPath, fileName).toString();
            final String oldFile = Paths.get(".", oldPath, fileName).toString();
            sftpConnection.getChannel().rename(oldFile, newFile);
            log.info("file {} moved from {} to {}", fileName, oldPath, newPath);
        } catch (final SftpException e) {
            throw new RuntimeException("SftpException while moving file " + fileName + " from " + oldPath + " to "
                    + newPath, e);
        } finally {
            disconnect();
        }
    }

    /**
     * Moves a file to another path on the SFTP server.
     *
     * @param path
     *            the current path of the file
     * @param oldFilename
     *            the old file name
     * @param newFileName
     *            the new file name
     */
    public synchronized void renameFile(final String path, final String oldFilename, final String newFileName) {
        connect();
        try {
            final String newFile = Paths.get(".", path, newFileName).toString();
            final String oldFile = Paths.get(".", path, oldFilename).toString();
            sftpConnection.getChannel().rename(oldFile, newFile);
            log.info("file {} renamed to {}", oldFilename, newFileName);
        } catch (final SftpException e) {
            throw new RuntimeException("SftpException while renaming file " + oldFilename + " to "
                    + newFileName, e);
        } finally {
            disconnect();
        }
    }

    /**
     * Retrieves all file names.
     *
     * @param path
     *            path to retrieve from
     * @return list with file names
     */
    public List<String> getFileNamesFromPath(final String path) {
        return getFileNamesFromPath(path, null);
    }

    /**
     * Retrieves all file names with date criteria.
     *
     * @param path
     *            path to retrieve from
     * @param onlyOlderThan
     *            date criteria
     * @return list with file names
     */
    public Map<String, Date> getFileNamesFromPathOlderThan(final String path, final Date onlyOlderThan) {
        return getFileNamesFromPath(path, null, onlyOlderThan);
    }

    /**
     * Retrieves all file names with file name pattern criteria
     *
     * @param path
     *            path to retrieve from
     * @param fileNamePattern
     *            file name pattern criteria
     * @return list with file names
     */
    public List<String> getFileNamesFromPath(final String path, final Pattern fileNamePattern) {
        return new ArrayList<>(getFileNamesFromPath(path, fileNamePattern, null).keySet());
    }

    /**
     * Returns all files in a folder on stfp-server.
     *
     * @param path
     *            relative target-path on the sftp-server (example: in/)
     * @param fileNamePattern
     *            file name pattern criteria
     * @param onlyOlderThan
     *            date criteria
     * @return list of file-names and last modification date
     * @throws RuntimeException
     */
    public synchronized Map<String, Date> getFileNamesFromPath(final String path, final Pattern fileNamePattern,
            final Date onlyOlderThan) {
        List<ChannelSftp.LsEntry> files = getFileListFromSftp(path);
        Map<String, Date> result = new HashMap<>();
        for (ChannelSftp.LsEntry e : files) {
            if (!e.getAttrs().isDir()) {
                if (fileNamePattern == null || fileNamePattern.matcher(e.getFilename()).matches()) {
                    Date lastModificationDate = new Date(e.getAttrs().getATime() * FACTOR_TO_SECONDS);
                    if (onlyOlderThan == null || lastModificationDate.before(onlyOlderThan)) {
                        result.put(e.getFilename(), new Date(e.getAttrs().getATime() * FACTOR_TO_SECONDS));
                    }
                }
            }
        }
        return result;
    }

    private List<ChannelSftp.LsEntry> getFileListFromSftp(final String path) {
        connect();
        try {
            return ls(path);
        } catch (final SftpException e) {
            throw new RuntimeException("SftpException while getting fileNames from path " + path, e);
        } finally {
            disconnect();
        }
    }

    /**
     * Check if file exists in path.
     *
     * @param path
     *            relative target-path on the sftp-server (example: in/)
     * @param fileName
     *            name of the file on the server (example: RSP1and16543000.CAS)
     * @return true if file exists
     * @throws RuntimeException
     *             if can not check existence
     */
    public synchronized Boolean fileExists(final String path, final String fileName) throws RuntimeException {
        connect();
        try {
            List<ChannelSftp.LsEntry> files = ls(path);

            for (final ChannelSftp.LsEntry file : files) {
                if (file.getFilename().equals(fileName)) {
                    return true;
                }
            }
        } catch (final SftpException e) {
            throw new RuntimeException("SftpException while checking existence of file " + fileName + " in path "
                    + path, e);
        } finally {
            disconnect();
        }
        return false;
    }

    private List<ChannelSftp.LsEntry> ls(String path) throws SftpException {
        String dirPath = Paths.get(".", path).toString();
        sftpConnection.getChannel().cd(dirPath);
        return sftpConnection.getChannel().ls("*");
    }

    /**
     * Return the content of a Streamfile.
     *
     * @param path
     *            relative target-path on the sftp-server (example: in/)
     * @param fileName
     *            name of the file on the server (example: RSP1and16543000.CAS)
     * @return content of the file
     * @throws RuntimeException
     */
    public synchronized byte[] getFileContent(final String path, final String fileName) {
        connect();
        final String targetFile = Paths.get(".", path, fileName).toString();

        try (InputStream input = sftpConnection.getChannel().get(targetFile)) {
            return IOUtils.toByteArray(input);
        } catch (final SftpException | IOException e) {
            throw new RuntimeException("Failed to retrieve fileContent for file " + fileName + " in path " + path,
                    e);
        } finally {
            disconnect();
        }
    }

    /**
     * Downloads a file from the SFTP server to the local disc.
     *
     * @param source
     *            relative file path on the SFTP server
     * @param destination
     *            file path on the local disc
     */
    public synchronized void downloadFile(Path source, Path destination) {
        connect();
        final File destFile = destination.toFile();
        try (InputStream input = sftpConnection.getChannel().get(source.toString()); OutputStream output = new FileOutputStream(destFile)) {
            IOUtils.copy(input, output);
        } catch (final Exception e) {
            // if the copy job was not successful, we delete the created Destination File
            FileUtils.deleteQuietly(destFile);
            throw new RuntimeException("Failed to download file " + source + " to " + destination, e);
        } finally {
            disconnect();
        }
    }

    /**
     * Check the connector state based on current local time, whether it is currently deactivated by configuration
     *
     * @return <code>true</code> if connector is currently active
     */
    public boolean isActive() {
        if (sftpConfiguration.getStartTime() == null || sftpConfiguration.getEndTime() == null
                || sftpConfiguration.getStartTime().equals(sftpConfiguration.getEndTime())) {
            return true;
        }

        LocalTime now = LocalTime.now();
        if (sftpConfiguration.getStartTime().isBefore(sftpConfiguration.getEndTime()) && now.isAfter(sftpConfiguration.getStartTime())
                && now.isBefore(sftpConfiguration.getEndTime())) {
            return true;
        } else if (sftpConfiguration.getStartTime().isAfter(sftpConfiguration.getEndTime())
                && (now.isAfter(sftpConfiguration.getStartTime()) || now.isBefore(sftpConfiguration.getEndTime()))) {
            return true;
        }
        return false;
    }

    public int getPriority() {
        return sftpConfiguration.getPriority();
    }

    /**
     * Open sftpConnection to a sftp-server.
     *
     * @throws RuntimeException
     */
    protected void connect() {
        try {
            sftpConnection = sftpConnectionFactory.create(sftpConfiguration);
        } catch (RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException("failed to connect to sftp-server.", e);
        }

    }

    /**
     * Close existing sftpConnection.
     */
    protected void disconnect() {
        if (sftpConfiguration != null) {
            log.trace("disconnecting as user {} (host: {}, port: {})", sftpConfiguration.getUsername(), sftpConfiguration.getHost(),
                    sftpConfiguration.getPort());
        }

        if (sftpConnection != null) {
            try {
                sftpConnectionFactory.destroyObject(sftpConnection);
            } catch (RuntimeException e) {
                throw new RuntimeException("Returning JSch Session to pool leads to Exception", e);
            }
            sftpConnection = null;
        }

        if (sftpConfiguration != null) {
            log.trace("disconnected as user {} (host: {}, port: {})", sftpConfiguration.getUsername(), sftpConfiguration.getHost(),
                    sftpConfiguration.getPort());
        }
    }


    private String getSftpExceptionErrorId(SftpException e) {
        switch (e.id) {
            case ChannelSftp.SSH_FX_OK:
                return "SSH_FX_OK";
            case ChannelSftp.SSH_FX_EOF:
                return "SSH_FX_EOF";
            case ChannelSftp.SSH_FX_NO_SUCH_FILE:
                return "SSH_FX_NO_SUCH_FILE";
            case ChannelSftp.SSH_FX_PERMISSION_DENIED:
                return "SSH_FX_PERMISSION_DENIED";
            case ChannelSftp.SSH_FX_FAILURE:
                return "SSH_FX_FAILURE";
            case ChannelSftp.SSH_FX_BAD_MESSAGE:
                return "SSH_FX_BAD_MESSAGE";
            case ChannelSftp.SSH_FX_NO_CONNECTION:
                return "SSH_FX_NO_CONNECTION";
            case ChannelSftp.SSH_FX_CONNECTION_LOST:
                return "SSH_FX_CONNECTION_LOST";
            case ChannelSftp.SSH_FX_OP_UNSUPPORTED:
                return "SSH_FX_OP_UNSUPPORTED";
            default:
                return "UNKNOWN_ID";
        }
    }

}
