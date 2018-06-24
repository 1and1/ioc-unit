package com.oneandone.ejbcdiunit.simulators.sftpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of JSch's logger that uses the SLF4J logger supplied by the container.
 */
public class JschLogger implements com.jcraft.jsch.Logger {

    private Logger log = LoggerFactory.getLogger("SftpLogger");

    @Override
    public boolean isEnabled(int level) {
        // No logging required if we don't use the real SFTP server. Saves Jenkins from huge log files.
        return true;
    }

    @Override
    public void log(int level, String message) {
        switch (level) {
            case com.jcraft.jsch.Logger.DEBUG:
                log.debug(message);
                break;
            case com.jcraft.jsch.Logger.INFO:
                log.info(message);
                break;
            case com.jcraft.jsch.Logger.WARN:
                log.warn(message);
                break;
            case com.jcraft.jsch.Logger.ERROR:
                log.error(message);
                break;
            case com.jcraft.jsch.Logger.FATAL:
                log.error(message);
                break;
            default:
                log.debug(message);
        }
    }

}
