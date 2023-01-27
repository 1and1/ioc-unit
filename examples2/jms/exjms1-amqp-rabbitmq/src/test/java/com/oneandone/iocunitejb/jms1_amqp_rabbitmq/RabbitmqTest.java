package com.oneandone.iocunitejb.jms1_amqp_rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.melowe.jms2.compat.Jms2ConnectionFactory;
import com.oneandone.iocunit.IocUnitRunner;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import com.rabbitmq.jms.client.message.RMQTextMessage;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
public class RabbitmqTest {

    @Test
    @Ignore
    public void jmsRabbitmqTest() {
//        ConnectionFactory oldcf = new ConnectionFactory();
//        oldcf.setHost("localhost");
//        oldcf.setUsername("guest");
//        oldcf.setPassword("guest");
//        oldcf.setVirtualHost("/");
//        oldcf.setPort(5672);
//        try (Connection conn = oldcf.newConnection()) {
//            try (Channel channel = conn.createChannel()) {
//                final String queue_name = "queue_name2";
//                channel.queueDeclare(queue_name, false, false, false, null);
//                String message = "Hello World!";
//                channel.basicPublish("", queue_name, null, message.getBytes());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
        RMQConnectionFactory cf = new RMQConnectionFactory();
        cf.setHost("localhost");
        cf.setUsername("guest");
        cf.setPassword("guest");
        cf.setVirtualHost("/");
        cf.setPort(5672);
        Queue queue = new RMQDestination("queue_name3", true, true);
        Jms2ConnectionFactory jms2Cf = new Jms2ConnectionFactory(cf);
        try (javax.jms.Connection connection = jms2Cf.createConnection()) {
            connection.start();

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                 Session session2 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                 MessageProducer mp = session.createProducer(queue);
                 MessageConsumer mc = session2.createConsumer(queue)) {
                Message message = mc.receive(10);
                if(message != null) {
                    System.out.println("message received before sending: " + message);
                }
                TextMessage tm = new RMQTextMessage();
                tm.setJMSExpiration(System.currentTimeMillis() + 100 * 1000);
                tm.setText("Hello World!");
                System.out.println("message sent before sending: " + tm.getText());
                mp.send(tm);
                // session.commit();
                message = mc.receive(10000);
                System.out.println("message received after sending: " + message);
            }
            connection.stop();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Ignore
    public void connectNativeRabbitmqTest() {
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost("localhost");
        cf.setUsername("guest");
        cf.setPassword("guest");
        cf.setVirtualHost("/");
        cf.setPort(5672);
        try (Connection conn = cf.newConnection()) {
            try (Channel channel = conn.createChannel()) {
                final String queue_name = "QUEUE_NAME";
                channel.queueDeclare(queue_name, false, false, false, null);
                String message = "Hello World!";
                channel.basicPublish("", queue_name, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String receivedMessage = new String(delivery.getBody(), "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                };
                channel.basicConsume(queue_name, true, deliverCallback, consumerTag -> {
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
