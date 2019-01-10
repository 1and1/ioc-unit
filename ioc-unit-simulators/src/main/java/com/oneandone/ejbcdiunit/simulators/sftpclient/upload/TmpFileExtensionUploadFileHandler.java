package com.oneandone.ejbcdiunit.simulators.sftpclient.upload;

import java.nio.file.Paths;

/**
 * Special {@link UploadFileHandler} using a temporary file with extension ".tmp"
 */
public class TmpFileExtensionUploadFileHandler extends AbstractTmpUploadFileHandler implements UploadFileHandler {

    private static final String TMP_FILE_EXTENSION = ".tmp";

    @Override
    public String getUploadFileName(String path, String fileName) {
        return Paths.get(WORK_FILE_DIRECTORY, path, fileName + TMP_FILE_EXTENSION).toString();
    }

}
