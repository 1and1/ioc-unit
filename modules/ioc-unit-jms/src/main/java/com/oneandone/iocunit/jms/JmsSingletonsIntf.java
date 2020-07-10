package com.oneandone.iocunit.jms;

import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Topic;

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
