package com.oneandone.iocunit.jms.rabbitmq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.melowe.jms2.compat.Jms2ConnectionFactory;
import com.melowe.jms2.compat.Jms2Message;
import com.melowe.jms2.compat.Jms2MessageListener;
import com.oneandone.iocunit.ejb.jms.JmsSingletonsIntf;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;

import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMq;
import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMqConfig;


/**
 * Manages the singleton used to mock JMS in CDI-Unit using rabbitmq.
 *
 * @author aschoerk
 */
@Singleton
public class RabbitMQSingletons implements JmsSingletonsIntf {

    private AtomicReference<Jms2ConnectionFactory> connectionFactoryAtomicReference = new AtomicReference<>();

    private AtomicReference<Connection> mdbConnection = new AtomicReference<Connection>();

    private Logger logger = LoggerFactory.getLogger("JmsFactory");

    private AtomicReference<Map<String, Destination>> destinations = new AtomicReference<>();


    static EmbeddedRabbitMq rabbitMq;

    @Override
    public Connection getConnection() {
        try {
            getConnectionFactory();

        } catch (Exception e) {
            if (rabbitMq == null) {
                EmbeddedRabbitMqConfig config = new EmbeddedRabbitMqConfig.Builder()
                        .rabbitMqServerInitializationTimeoutInMillis(10000)
                        .build();

                rabbitMq = new EmbeddedRabbitMq(config);
                rabbitMq.start();
                try {
                    final Connection connection =  this.connectionFactoryAtomicReference.get().createConnection();
                    connection.start();
                    destinations.set(new ConcurrentHashMap<>());
                    mdbConnection.set(connection);

                } catch (Exception embedding) {
                    throw new RuntimeException(embedding);
                }
            } else {
                throw new RuntimeException(e);
            }
        }
        return mdbConnection.get();
    }

    @Produces
    JMSContext createJMSContext() {
        try {
            getConnectionFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return connectionFactoryAtomicReference.get().createContext();
    }


    /**
     * make sure a new weld-container can be created without residue
     */
    @PreDestroy
    @Override
    public void destroy() {
        try {
            mdbConnection.get().close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Queue createQueue(String name) {
        if (!destinations.get().containsKey(name)) {
            destinations.get().put(name, new RMQDestination(name, true, true));
        }
        return (Queue) destinations.get().get(name);
    }

    @Override
    public Topic createTopic(String name) {
        if (!destinations.get().containsKey(name)) {
            destinations.get().put(name, new RMQDestination(name, false, true));
        }
        return (Topic) destinations.get().get(name);
    }

    /**
     * creates the jms-connectionfactory which is injected anywhere during the tests.
     *
     * @return one ConnectionFactory able to create mockrunner-jms-objects
     * @throws Exception should not occur since mockrunner uses the main memory for jms.
     */
    @Override
    public ConnectionFactory getConnectionFactory() throws Exception {
        if(connectionFactoryAtomicReference.get() == null) {
            RMQConnectionFactory cf = new RMQConnectionFactory();
            cf.setHost("localhost");
            cf.setUsername("guest");
            cf.setPassword("guest");
            cf.setVirtualHost("/");
            cf.setPort(5672);
            cf.setCleanUpServerNamedQueuesForNonDurableTopicsOnSessionClose(true);
            final Jms2ConnectionFactory jms2Cf = new Jms2ConnectionFactory(cf);
            if(connectionFactoryAtomicReference.compareAndSet(null, jms2Cf)) {
                final Connection connection = jms2Cf.createConnection();
                connection.start();
                destinations.set(new ConcurrentHashMap<>());
                mdbConnection.set(connection);
            }
        }
        return connectionFactoryAtomicReference.get();
    }

    @Override
    public void jms2OnMessage(final MessageListener listener, final Message message) {
        if(message instanceof Jms2Message) {
            listener.onMessage(message);
        }
        else {
            new Jms2MessageListener(listener).onMessage(message);
        }
    }

}
