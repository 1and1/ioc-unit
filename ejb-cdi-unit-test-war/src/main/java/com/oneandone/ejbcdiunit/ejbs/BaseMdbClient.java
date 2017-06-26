package com.oneandone.ejbcdiunit.ejbs;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.slf4j.Logger;

/**
 * @author aschoerk
 */
public class BaseMdbClient {

    @Inject
    Logger logger;

    @Resource(lookup = "java:/ConnectionFactory")
    ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/test/env/myQueue1")
    Queue queue;

    @Resource(lookup = "java:/test/env/myTopic")
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }

    }
}
