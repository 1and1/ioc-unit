package com.oneandone.ejbcdiunit.simulators.sftpclient.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Factory to create {@link UploadFileHandler}
 */
public class UploadFileHandlerFactory {

    private final Logger log = LoggerFactory.getLogger(UploadFileHandlerFactory.class);

    /**
     * creates {@link UploadFileHandler} depending on {@link UploadFileStrategy}
     * 
     * @param uploadFileStrategy
     *            {@link UploadFileStrategy}
     * @return {@link UploadFileHandler}
     */
    public UploadFileHandler getUploadFileHandler(UploadFileStrategy uploadFileStrategy) {
        if (uploadFileStrategy == null) {
            // could be the case if mock tests are used and mock does not return UploadFileStrategy
            log.error("uploadFileStrategy is null at creating UploadFileHandler");
            throw new RuntimeException("uploadFileStrategy is null at creating UploadFileHandler");
        }
        return uploadFileStrategy.getUploadFileHandler();
    }
}
