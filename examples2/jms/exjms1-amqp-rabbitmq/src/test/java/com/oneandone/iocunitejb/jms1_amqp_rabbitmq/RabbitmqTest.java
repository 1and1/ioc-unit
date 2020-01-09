package com.oneandone.iocunitejb.jms1_amqp_rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.jms.admin.RMQConnectionFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
public class RabbitmqTest {

    @Test
    @Ignore
    public void jmsRabbitmqTest() {

        RMQConnectionFactory cf = new RMQConnectionFactory();
        cf.setHost("localhost");
        cf.setUsername("guest");
        cf.setPassword("guest");
        cf.setVirtualHost("/");
        cf.setPort(5672);

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
                channel.basicConsume(queue_name, true, deliverCallback, consumerTag -> { });
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
