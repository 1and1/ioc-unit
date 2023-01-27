package com.oneandone.iocunitejb.ejbs;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

import org.slf4j.Logger;

/**
 * @author aschoerk
 */
public class BaseMdbClient {

    @Inject
    Logger logger;

    @JMSConnectionFactory("jms/connectionFactory")
    JMSContext context;

    @Resource(lookup = "openejb:Resource/MyConnectionFactory")
    ConnectionFactory connectionFactory;

    @Resource(lookup = "openejb:Resource/myQueue1")
    Queue queue;

    @Resource(lookup = "openejb:Resource/myTopic")
    Topic topic;

    /**
     * sends a number of default messages to queue
     */
    public void sendMessageToQueue() {

        try (Connection connection = connectionFactory.createConnection()) {
            try (Session session = connection.createSession()) {

                try (MessageProducer messageProducer = session.createProducer(queue)) {
                    TextMessage message = session.createTextMessage();
                    for (int i = 0; i < 10; i++) {
                        message.setText("This is queue message " + (i + 1));
                        message.setBooleanProperty("ForQMdbEjb", (i + 1) % 2 == 1);
                        message.setBooleanProperty("ForQMdbEjb2", (i + 1) % 2 == 0);
                        logger.info("Sending message: {}", message.getText());
                        messageProducer.send(message);
                    }
                }
            }
        } catch (JMSException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }

    }

    /**
     * sends a number of default messages to queue
     */
    public void sendMessageViaContextToQueue() {
        try (JMSContext context = connectionFactory.createContext()) {
            TextMessage message = context.createTextMessage();
            for (int i = 0; i < 10; i++) {
                message.setText("This is queue message " + (i + 1));
                message.setBooleanProperty("ForQMdbEjb", (i + 1) % 2 == 1);
                message.setBooleanProperty("ForQMdbEjb2", (i + 1) % 2 == 0);
                logger.info("Sending message: {}", message.getText());
                context.createProducer().send(queue, message);
            }
        } catch (JMSException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }

    }

    /**
     * sends a number of default messages to queue
     */
    public void sendMessageViaInjectedContextToQueue() {
        try {
            TextMessage message = context.createTextMessage();
            for (int i = 0; i < 10; i++) {
                message.setText("This is queue message " + (i + 1));
                message.setBooleanProperty("ForQMdbEjb", (i + 1) % 2 == 1);
                message.setBooleanProperty("ForQMdbEjb2", (i + 1) % 2 == 0);
                logger.info("Sending message: {}", message.getText());
                context.createProducer().send(queue, message);
            }
        } catch (JMSException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }

    }

    public void sendMessageViaContextToTopic() {
        try (JMSContext context = connectionFactory.createContext()) {
            TextMessage message = context.createTextMessage();
            for (int i = 0; i < 10; i++) {
                message.setText("This is topic message " + (i + 1));
                logger.info("Sending message: {} to topic", message.getText());
                context.createProducer().send(topic, message);
            }

        } catch (JMSException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }


    /**
     * sends a number of default messages to topic
     */
    public void sendMessageToTopic() {

        try (Connection connection = connectionFactory.createConnection()) {
            try (Session session = connection.createSession()) {
                try (MessageProducer messageProducer = session.createProducer(topic)) {
                    TextMessage message = session.createTextMessage();
                    for (int i = 0; i < 10; i++) {
                        message.setText("This is topic message " + (i + 1));
                        logger.info("Sending message: {} to topic", message.getText());
                        messageProducer.send(message);
                    }
                }
            }
        } catch (JMSException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}
