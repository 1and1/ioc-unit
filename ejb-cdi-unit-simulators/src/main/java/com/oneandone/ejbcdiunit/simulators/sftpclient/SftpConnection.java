package com.oneandone.ejbcdiunit.simulators.sftpclient;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpConnection {

    private Session session;
    private ChannelSftp channel;
    private String pwd;

    public SftpConnection(Session session, ChannelSftp channel) throws SftpException {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        if (channel == null) {
            throw new IllegalArgumentException("channel must not be null");
        }

        this.session = session;
        this.channel = channel;
        pwd = channel.pwd();
    }

    public Session getSession() {
        return session;
    }

    public ChannelSftp getChannel() {
        return channel;
    }

    public String getPwd() {
        return pwd;
    }

}
