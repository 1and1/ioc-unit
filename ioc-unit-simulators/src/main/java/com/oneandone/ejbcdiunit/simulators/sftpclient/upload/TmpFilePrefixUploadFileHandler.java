package com.oneandone.ejbcdiunit.simulators.sftpclient.upload;

import java.nio.file.Paths;

/**
 * Sepcial {@link UploadFileHandler} to upload file to temporary file with file-prefix first and final rename it to target file
 */
public class TmpFilePrefixUploadFileHandler extends AbstractTmpUploadFileHandler implements UploadFileHandler {

    /**
     * see Telefonica Spec page 107, chapter 6.5.3
     */
    private static final String TMP_FILE_PREFIX = "tmp_";

    @Override
    public String getUploadFileName(String path, String fileName) {
        return Paths.get(WORK_FILE_DIRECTORY, path, TMP_FILE_PREFIX + fileName).toString();
    }

}
