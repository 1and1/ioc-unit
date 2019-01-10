package com.oneandone.ejbcdiunit.simulators.sftpclient.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * Common methods if a temporary file for uploading is used.
 */
public abstract class AbstractTmpUploadFileHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTmpUploadFileHandler.class);

    /**
     * rename temporary uploadFile to targetFile
     * 
     * @param channel
     *            {@link ChannelSftp}
     * @param uploadFileName
     *            temporary file name
     * @param targetFileName
     *            final target file name
     * @throws SftpException
     *             if renaming fails
     */
    public void doRenameToTargetFile(ChannelSftp channel, String uploadFileName, String targetFileName) throws SftpException {
        channel.rename(uploadFileName, targetFileName);
        LOG.trace("Renaming {} to {} was successful.", uploadFileName, targetFileName);
    }

    /**
     * remove temporary uploaded file in case of exception
     * 
     * @param channel
     *            {@link ChannelSftp}
     * @param uploadFileName
     *            temporary fileName
     */
    private void removeTemporaryFileAtException(ChannelSftp channel, String uploadFileName) {
        try {
            channel.rm(uploadFileName);
        } catch (Exception exRemove) {
            LOG.warn("could not remove temp file " + uploadFileName, exRemove);
        }
    }

    /**
     * create error message for handling temporary files.
     * 
     * @param isUploadSuccessful
     *            true if only renaming from temporary file to target file fails
     * @param uploadFileName
     *            temporary file name
     * @param targetFileName
     *            final target file name
     * @return error message
     */
    private String createErrorMessage(boolean isUploadSuccessful, String uploadFileName, String targetFileName) {
        if (isUploadSuccessful) {
            // Do not change the text "failed renaming" because it is
            // used to distinguish the exception later.
            return "failed renaming " + uploadFileName + " to " + targetFileName;
        } else {
            return "failed uploading file " + uploadFileName;
        }
    }

    /**
     * @param channel
     *            {@link ChannelSftp}
     * @param uploadFileName
     *            temporary fileName
     * @param targetFileName
     *            final target fileName
     * @param isUploadSuccessful
     *            true if file upload was successful, but renaming to targetFilename fails
     * @return exception message
     */
    public String doExceptionHandling(ChannelSftp channel, String uploadFileName, String targetFileName, boolean isUploadSuccessful) {
        removeTemporaryFileAtException(channel, uploadFileName);
        removeTemporaryFileAtException(channel, targetFileName);
        return createErrorMessage(isUploadSuccessful, uploadFileName, targetFileName);
    }

}
