package com.oneandone.ejbcdiunit.simulators.sftpclient.upload;

import java.nio.file.Paths;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * {@link UploadFileHandler} to handle no temporary files. Files are directly uploaded to targetFile.
 */
public class NoTmpUploadFileHandler implements UploadFileHandler {

    @Override
    public String getUploadFileName(String path, String fileName) {
        return Paths.get(WORK_FILE_DIRECTORY, path, fileName).toString();
    }

    @Override
    public void doRenameToTargetFile(ChannelSftp channel, String uploadFileName, String targetFileName) throws SftpException {
        // do nothing because no temporary file existing
    }

    @Override
    public String doExceptionHandling(ChannelSftp channel, String uploadFileName, String targetFile, boolean isUploadSuccessful) {
        return "failed uploading file " + targetFile;
    }

}
