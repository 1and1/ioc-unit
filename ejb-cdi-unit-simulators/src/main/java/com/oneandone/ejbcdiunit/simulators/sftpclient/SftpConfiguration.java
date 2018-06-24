package com.oneandone.ejbcdiunit.simulators.sftpclient;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.oneandone.ejbcdiunit.simulators.sftpclient.upload.UploadFileStrategy;


public abstract class SftpConfiguration {


    protected static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("k:m");
    private static final Pattern DEFAULT_NAME_PATTERN = Pattern.compile(".*");

    public abstract String getHost();

    public abstract Integer getPort();

    public abstract String getPath();

    public abstract String getUsername();

    public abstract String getPassword();

    public abstract Integer getRetrySleep();

    public abstract Integer getRetries();

    public abstract Integer getTimeout();

    public abstract String getStrictHostKeySetting();

    public abstract String getPrivateKeyPath();

    public Pattern getFileNamePattern() {
        return DEFAULT_NAME_PATTERN;
    }

    /**
     * return uploadFileStrategy, e.g. if a tmp-file is used or not
     *
     * @return {@link UploadFileStrategy}
     */
    public abstract UploadFileStrategy getUploadFileStrategy();

    public abstract Integer getMinIdleConnections();

    /**
     * if &gt; 0 then pooling is enabled
     *
     * @return
     */
    public abstract Integer getMaxIdleConnections();

    /**
     * if &gt; 0 then pooling is enabled
     *
     * @return
     */
    public abstract Integer getMaxNumberOfConnections();

    public abstract boolean isHystrixEnabled();

    public LocalTime getStartTime() {
        return null;
    }

    public LocalTime getEndTime() {
        return null;
    }

    public int getPriority() {
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getClass().hashCode();
        result = prime * result + ((getHost() == null) ? 0 : getHost().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getPrivateKeyPath() == null) ? 0 : getPrivateKeyPath().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getPort() == null) ? 0 : getPort().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        SftpConfiguration other = (SftpConfiguration) obj;
        if (getHost() == null) {
            if (other.getHost() != null) {
                return false;
            }
        } else if (!getHost().equals(other.getHost())) {
            return false;
        }
        if (getPassword() == null) {
            if (other.getPassword() != null) {
                return false;
            }
        } else if (!getPassword().equals(other.getPassword())) {
            return false;
        }
        if (getPrivateKeyPath() == null) {
            if (other.getPrivateKeyPath() != null) {
                return false;
            }
        } else if (!getPrivateKeyPath().equals(other.getPrivateKeyPath())) {
            return false;
        }
        if (getPath() == null) {
            if (other.getPath() != null) {
                return false;
            }
        } else if (!getPath().equals(other.getPath())) {
            return false;
        }

        if (getPort() == null) {
            if (other.getPort() != null) {
                return false;
            }
        } else if (!getPort().equals(other.getPort())) {
            return false;
        }
        if (getUsername() == null) {
            if (other.getUsername() != null) {
                return false;
            }
        } else if (!getUsername().equals(other.getUsername())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [host=" + getHost() + ", port=" + getPort()
                + ", userName=" + getUsername() + ", pathIn=" + getPath() + "]";
    }

}
