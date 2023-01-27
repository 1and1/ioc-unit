package com.oneandone.iocunit.jms.activemq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.melowe.jms2.compat.Jms2ConnectionFactory;
import com.melowe.jms2.compat.Jms2Message;
import com.melowe.jms2.compat.Jms2MessageListener;
import com.oneandone.iocunit.jms.JmsSingletonsIntf;


/**
 * Manages the singleton used to mock JMS in CDI-Unit using rabbitmq.
 *
 * @author aschoerk
 */
@ApplicationScoped
public class ActiveMQSingletons implements JmsSingletonsIntf {

    private AtomicReference<Jms2ConnectionFactory> connectionFactoryAtomicReference = new AtomicReference<>();

    private AtomicReference<Connection> mdbConnection = new AtomicReference<Connection>();

    private Logger logger = LoggerFactory.getLogger("JmsFactory");

    private AtomicReference<Map<String, Destination>> destinations = new AtomicReference<>();


    @Override
    public Connection getConnection() {
        try {
            getConnectionFactory();

        } catch (Exception e) {
            throw new RuntimeException(e);
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
        if(!destinations.get().containsKey(name)) {
            try (Session session = getConnection().createSession()) {
                destinations.get().put(name, session.createQueue(name));
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
        return (Queue) destinations.get().get(name);
    }

    @Override
    public Topic createTopic(String name) {
        if(!destinations.get().containsKey(name)) {
            try (Session session = getConnection().createSession()) {
                destinations.get().put(name, session.createTopic(name));
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
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
            Jms2ConnectionFactory cf = new Jms2ConnectionFactory(new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false"));

            if(connectionFactoryAtomicReference.compareAndSet(null, cf)) {
                final Connection connection = cf.createConnection();
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
