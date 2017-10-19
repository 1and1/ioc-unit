package com.oneandone.ejbcdiunit.simulators.sftpserver;

import java.util.function.Consumer;

import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.subsystem.sftp.SftpEventListener;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

/**
 * Creates {@link DelayedSftpSubsystem}s instead of {@link org.apache.sshd.server.subsystem.sftp.SftpSubsystem}s.
 */
public class DelayedSftpSubsystemFactory extends SftpSubsystemFactory {

    @Override
    public Command create() {
        DelayedSftpSubsystem subsystem = new DelayedSftpSubsystem(getExecutorService(), isShutdownOnExit(), getUnsupportedAttributePolicy());
        GenericUtils.forEach(getRegisteredListeners(), new Consumer<SftpEventListener>() {
            @Override
            public void accept(SftpEventListener sftpEventListener) {
                addSftpEventListener(sftpEventListener);
            }
        });
        return subsystem;
    }

}
