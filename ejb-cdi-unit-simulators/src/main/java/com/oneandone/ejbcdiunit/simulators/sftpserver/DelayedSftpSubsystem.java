package com.oneandone.ejbcdiunit.simulators.sftpserver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.server.subsystem.sftp.SftpErrorStatusDataHandler;
import org.apache.sshd.server.subsystem.sftp.SftpFileSystemAccessor;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystem;
import org.apache.sshd.server.subsystem.sftp.UnsupportedAttributePolicy;

/**
 * {@link SftpSubsystem} with a configurable delay time. Processing of each command halts for this amount of time.
 */
public class DelayedSftpSubsystem extends SftpSubsystem {

    private static final AtomicInteger DELAY_MILLIS = new AtomicInteger();

    public DelayedSftpSubsystem(ExecutorService executorService, boolean shutdownOnExit, UnsupportedAttributePolicy policy) {
        super(executorService,
                shutdownOnExit,
                policy,
                new SftpFileSystemAccessor() {

                },
                new SftpErrorStatusDataHandler(){});

    }

    public static void setDelay(int delayMillis) {
        DelayedSftpSubsystem.DELAY_MILLIS.set(delayMillis);
    }

    @Override
    protected void process(Buffer buffer) throws IOException {
        try {
            Thread.sleep(DELAY_MILLIS.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        super.process(buffer);
    }

}
