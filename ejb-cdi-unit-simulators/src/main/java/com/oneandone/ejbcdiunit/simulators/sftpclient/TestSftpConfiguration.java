package com.oneandone.ejbcdiunit.simulators.sftpclient;

import java.time.LocalTime;
import java.util.regex.Pattern;

import com.oneandone.ejbcdiunit.simulators.sftpclient.upload.UploadFileStrategy;

/**
 * @author aschoerk
 */
public class TestSftpConfiguration extends SftpConfiguration {

    private String host;
    private int port;
    private String userName;
    private String password;
    private Integer connectionRetrySleep;
    private Integer connectionRetries;
    private Integer connectionTimeout;
    private String strictHostKeyChecking;
    private String pathOut;
    private Pattern fileNamePattern;
    private LocalTime startTime;
    private LocalTime endTime;
    private int priority;
    private String privateKeyPath;
    private String uploadFileStrategyConfig;
    private Integer maxTotalConnections;
    private Integer maxIdleConnections;
    private Integer minIdleConnections;
    private boolean hystrixEnabled;

    /**
     * Initializes the {@link TestSftpConfiguration} with the given parameters.
     * 
     * @param hostIpOrNameParameter
     *            address of the host (example: localhost)
     * @param portParameter
     *            port of the host(example: 22)
     * @param userParameter
     *            userName for connection (example: testuser)
     * @param passParameter
     *            password for connection (example: abc123)
     * @param connectionRetrySleepParameter
     *            time to sleep between connection retries
     * @param connectionRetriesParameter
     *            how many retries if connection fails
     * @param connectionTimeoutParameter
     *            timeout of the connection
     * @param strictHostKeyCheckingParameter
     *            use strict host key
     * @param privateKeyPathParameter
     *            path and file with private key
     * @param fileNamePattern
     *            pattern to filter files by
     * @param pathOutParameter
     *            default destination path for uploading files
     * @param startTimeParameter
     *            connector start-time on a daily base, leave empty or set equal to <code>endtime</code> for 24/7 activation
     * @param endTimeParameter
     *            connector end-time on a daily base, leave empty or set equal to <code>endtime</code> for 24/7 activation
     * @param priorityParameter
     *            custom priority in case of using multiple {@link TestSftpConfiguration}s
     * @param uploadFileStrategyConfig
     *            configuration Value of upload File Strategy
     * @param maxTotalConnections
     *            the maximum number of parallel sftp-connections.
     * @param maxIdleConnections
     *            the maximum number of not used, but not disconnected sftp-connections
     * @param minIdleConnections
     *            the minimum number of sftp-connections in connection-pool, that should be opened
     * @param hystrixEnabled
     */
    public TestSftpConfiguration(
            final String hostIpOrNameParameter,
            final Integer portParameter,
            final String userParameter,
            final String passParameter,
            final Integer connectionRetrySleepParameter,
            final Integer connectionRetriesParameter,
            final Integer connectionTimeoutParameter,
            final String strictHostKeyCheckingParameter,
            final String privateKeyPathParameter,
            final Pattern fileNamePattern,
            final String pathOutParameter,
            final LocalTime startTimeParameter,
            final LocalTime endTimeParameter,
            final int priorityParameter,
            final String uploadFileStrategyConfig,
            final Integer maxTotalConnections,
            final Integer maxIdleConnections,
            final Integer minIdleConnections,
            final boolean hystrixEnabled) {
        this.host = hostIpOrNameParameter;
        this.port = portParameter;
        this.userName = userParameter;
        this.password = passParameter;
        this.connectionRetrySleep = connectionRetrySleepParameter;
        this.connectionRetries = connectionRetriesParameter;
        this.connectionTimeout = connectionTimeoutParameter;
        this.strictHostKeyChecking = strictHostKeyCheckingParameter;
        this.fileNamePattern = fileNamePattern;
        this.pathOut = pathOutParameter;
        this.startTime = startTimeParameter;
        this.endTime = endTimeParameter;
        this.priority = priorityParameter;
        this.privateKeyPath = privateKeyPathParameter;
        this.uploadFileStrategyConfig = uploadFileStrategyConfig;
        this.maxTotalConnections = maxTotalConnections;
        this.maxIdleConnections = maxIdleConnections;
        this.minIdleConnections = minIdleConnections;
        this.hystrixEnabled = hystrixEnabled;
    }

    @Override
    public Integer getMaxIdleConnections() {
        return maxIdleConnections;
    }

    @Override
    public Integer getMaxNumberOfConnections() {
        return maxTotalConnections;
    }

    @Override
    public Integer getMinIdleConnections() {
        return minIdleConnections;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public String getPath() {
        return pathOut;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Integer getRetrySleep() {
        return connectionRetrySleep;
    }

    @Override
    public Integer getRetries() {
        return connectionRetries;
    }

    @Override
    public Integer getTimeout() {
        return connectionTimeout;
    }

    @Override
    public String getStrictHostKeySetting() {
        return strictHostKeyChecking;
    }

    @Override
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    @Override
    public Pattern getFileNamePattern() {
        return fileNamePattern;
    }

    @Override
    public UploadFileStrategy getUploadFileStrategy() {
        return UploadFileStrategy.getByConfigValue(uploadFileStrategyConfig);
    }

    @Override
    public boolean isHystrixEnabled() {
        return hystrixEnabled;
    }
}
