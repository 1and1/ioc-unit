package com.oneandone.ejbcdiunit.simulators.sftpclient.upload;

/**
 * enum containing file upload strategy, consists of configValue and Class to handle file upload.
 */
public enum UploadFileStrategy {
    NoTempFile(
            "NoTmpFile",
            new NoTmpUploadFileHandler()),

    TmpFileExtension(
            "TmpFileExtension",
            new TmpFileExtensionUploadFileHandler()),

    TmpFilePrefix(
            "TmpFilePrefix",
            new TmpFilePrefixUploadFileHandler()),

    TempDirectory(
            "TempDirectory",
            new UploadFileInTempDirectoryHandler());

    /**
     * possible configValues: NoTmpFile, TmpFileExtension, TmpFilePrefix, TempDirectory
     */
    private String configValue;
    /**
     * {@link UploadFileHandler}
     */
    private UploadFileHandler uploadFileHandler;

    /**
     * constructor
     * 
     * @param configValue
     *            configuration value
     * @param uploadFileHandler
     *            {@link UploadFileHandler} class to handle upload strategy
     */
    UploadFileStrategy(String configValue, UploadFileHandler uploadFileHandler) {
        this.configValue = configValue;
        this.uploadFileHandler = uploadFileHandler;
    }

    /**
     * return {@link UploadFileStrategy} given by configValue
     *
     * @param configValue
     *            configuration value used in _default.properties
     * @return {@link UploadFileStrategy}
     * @throws IllegalArgumentException
     *             if no valid configuration value is used
     */
    public static UploadFileStrategy getByConfigValue(String configValue) {
        for (UploadFileStrategy uploadFileHandlingStrategy : values()) {
            if (uploadFileHandlingStrategy.getConfigValue().equalsIgnoreCase(configValue)) {
                return uploadFileHandlingStrategy;
            }
        }
        throw new IllegalArgumentException("No UploadFileHandlingStrategy with configValue '" + configValue + "' found.");
    }

    /**
     * @return configured value
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * @return {@link UploadFileHandler}
     */
    public UploadFileHandler getUploadFileHandler() {
        return uploadFileHandler;
    }
}
