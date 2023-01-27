package com.oneandone.iocunit.jms;

import jakarta.annotation.PreDestroy;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.Topic;

/**
 * @author aschoerk
 */
public interface JmsSingletonsIntf {
    Connection getConnection();

    @PreDestroy
    void destroy();

    Queue createQueue(String name);

    Topic createTopic(String name);

    ConnectionFactory getConnectionFactory() throws Exception;

    void jms2OnMessage(MessageListener listener, Message message);
}
