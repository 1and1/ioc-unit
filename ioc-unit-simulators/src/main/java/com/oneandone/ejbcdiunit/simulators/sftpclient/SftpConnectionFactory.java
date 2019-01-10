package com.oneandone.ejbcdiunit.simulators.sftpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author aschoerk
 */
public class SftpConnectionFactory {

    protected final Logger log = LoggerFactory.getLogger(SftpConnectionFactory.class);


    /**
     * First char is the original from the password, and the other chars are replaced by asterisks.
     *
     * @return password for logging
     */
    private String getPasswordWithAsterisks(SftpConfiguration sftpConfiguration) {
        return (null == sftpConfiguration.getPassword()) ? null : sftpConfiguration.getPassword().replaceAll(".", "*");
    }

    public SftpConnection create(SftpConfiguration sftpConfiguration) throws Exception {
        Session session = null;
        ChannelSftp channel = null;
        for (int retryNr = 0; retryNr < sftpConfiguration.getRetries(); retryNr++) {

            try {
                session = getSession(sftpConfiguration);
                channel = (ChannelSftp) connectChannel(sftpConfiguration, session);
                SftpConnection sftpConnection = new SftpConnection(session, channel);
                log.info("Created Session for {}.", sftpConfiguration);
                return sftpConnection;

            } catch (final Exception e) {
                if (session != null) {
                    session.disconnect();
                }
                final String privateKeyPath = sftpConfiguration.getPrivateKeyPath();
                if (privateKeyPath != null && privateKeyPath.length() > 0) {
                    log.warn("unable to connect with keyfile: {} and passphrase {} to {}:{} - retry: {}",
                            sftpConfiguration.getPrivateKeyPath(), getPasswordWithAsterisks(sftpConfiguration), sftpConfiguration.getHost(),
                            sftpConfiguration.getPort(), retryNr, e);
                } else {
                    log.warn("unable to connect: {}:{}@{}:{} - retry: {}", sftpConfiguration.getUsername(),
                            getPasswordWithAsterisks(sftpConfiguration),
                            sftpConfiguration.getHost(),
                            sftpConfiguration.getPort(), retryNr, e);
                }

                if (e.getMessage() != null && e.getMessage().contains("Auth fail")) {
                    log.error(
                            "authentication failed (no more retries): host: {}, port: {}, userName: {}, password: {}, privateKeyPath: {}",
                            sftpConfiguration.getHost(), sftpConfiguration.getPort(), sftpConfiguration.getUsername(),
                            getPasswordWithAsterisks(sftpConfiguration),
                            sftpConfiguration.getPrivateKeyPath());
                    throw new RuntimeException("unable to connect to sftp-server.", e);
                }

                // cleanup, sleep, retry...
                try {
                    Thread.sleep(sftpConfiguration.getRetrySleep());
                } catch (final InterruptedException e2) {
                    log.info(e2.getMessage(), e2);
                    // make sure the Thread ends its processing as fast as possible. If Job is sending, Job should get retried later,
                    // if Receiving try next period. If Check: Failure is reported as early as possible.
                    throw new RuntimeException(e2);
                }
            }
            if (session != null) {
                session.disconnect();
            }
        }
        throw new Exception("Could not create Session on SftpConfiguration: " + sftpConfiguration);
    }

    private Channel connectChannel(SftpConfiguration sftpConfiguration, final Session pSession) throws JSchException {
        Channel pChannel = pSession.openChannel("sftp");
        pChannel.connect(sftpConfiguration.getTimeout());
        if (pChannel.isConnected()) {
            log.trace("connected as user {} (host: {}, port: {})", sftpConfiguration.getUsername(),
                    sftpConfiguration.getHost(), sftpConfiguration.getPort());
        }
        return pChannel;
    }

    private Session getSession(SftpConfiguration sftpConfiguration) throws JSchException {
        Session pSession = null;
        JSch jsch = new JSch();
        final String privateKeyPath = sftpConfiguration.getPrivateKeyPath();
        final String password = sftpConfiguration.getPassword();
        if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
            try {
                if (password == null || password.isEmpty()) {
                    jsch.addIdentity(sftpConfiguration.getPrivateKeyPath());
                    log.debug("using " + sftpConfiguration.getPrivateKeyPath() + " without passphrase");
                } else {
                    jsch.addIdentity(sftpConfiguration.getPrivateKeyPath(), password);
                    log.debug("using " + sftpConfiguration.getPrivateKeyPath() + " with passphrase " + getPasswordWithAsterisks(sftpConfiguration));
                }
            } catch (JSchException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        pSession = jsch.getSession(sftpConfiguration.getUsername(), sftpConfiguration.getHost(), sftpConfiguration.getPort());
        // use PK-file auth if available
        if (privateKeyPath == null || privateKeyPath.isEmpty()) {
            pSession.setPassword(password);
        }
        // TODO check why yes doesn't work
        // => maybe not in known hosts? http://stackoverflow.com/a/2003460 ff
        pSession.setConfig("StrictHostKeyChecking", sftpConfiguration.getStrictHostKeySetting());
        pSession.setTimeout(sftpConfiguration.getTimeout());
        pSession.connect();
        return pSession;
    }


    /**
     * This is called when closing the pool object
     *
     * @param p
     *            The SftpConnection to be closed
     */

    public void destroyObject(SftpConnection p) {
        p.getChannel().disconnect();
        p.getSession().disconnect();
    }
}
