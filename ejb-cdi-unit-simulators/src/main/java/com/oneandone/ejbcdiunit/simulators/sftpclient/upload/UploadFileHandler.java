package com.oneandone.ejbcdiunit.simulators.sftpclient.upload;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;


/**
 * Handler to handle uploading Files in SftpConnector. There are different implementations like uploading in temporary directory or uploading
 * in temporary files first.
 */
public interface UploadFileHandler {

    /**
     * constant for workFileDirectory, needed by method {@link #getUploadFileName(String, String)}
     */
    String WORK_FILE_DIRECTORY = ".";

    /**
     * return the temporary or not temporary uploadFileName
     * 
     * @param path
     *            Path
     * @param fileName
     *            target FileName
     * @return temporary or not temporary uploadFileName used in SftpConnector
     */
    String getUploadFileName(String path, String fileName);

    /**
     * rename uploadFileName to targetFileName, if uploadFile is a temporary file. If no temporary file is used, do nothing.
     * 
     * @param channel
     *            {@link ChannelSftp}
     * @param uploadFileName
     *            temporary fileName
     * @param targetFileName
     *            final target File Name
     * @throws SftpException
     *             if renaming fails
     */
    void doRenameToTargetFile(ChannelSftp channel, String uploadFileName, String targetFileName) throws SftpException;

    /**
     * do specialized ExceptionHandling depending on uploading strategy.
     * 
     * @param channel
     *            {@link ChannelSftp}
     * @param uploadFileName
     *            (temporary) fileName
     * @param targetFile
     *            target Filename
     * @param isUploadSuccessful
     *            true if upLoad of temporary file was successful and only the renaming to final target file fails
     * @return exception message depending on uploading strategy and isUploadSuccessful
     */
    String doExceptionHandling(ChannelSftp channel, String uploadFileName, String targetFile, boolean isUploadSuccessful);

}
