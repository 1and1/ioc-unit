package com.oneandone.ejbcdiunit.simulators.sftpclient.upload;

import java.nio.file.Paths;


/**
 * Specialized {@link UploadFileHandler} to upload to temporary directory first and then rename the file to final target file.
 */
public class UploadFileInTempDirectoryHandler extends AbstractTmpUploadFileHandler implements UploadFileHandler {

    private static final String TEMP_DIRECTORY = "temp";

    @Override
    public String getUploadFileName(String path, String fileName) {
        return Paths.get(WORK_FILE_DIRECTORY, path, TEMP_DIRECTORY, fileName).toString();
    }

}
