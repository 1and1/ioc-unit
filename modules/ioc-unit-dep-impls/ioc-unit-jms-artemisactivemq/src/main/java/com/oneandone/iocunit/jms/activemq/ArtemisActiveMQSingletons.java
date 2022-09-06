package com.oneandone.iocunit.jms.activemq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.jms.JmsSingletonsIntf;


/**
 * Manages the singleton used to mock JMS in CDI-Unit using rabbitmq.
 *
 * @author aschoerk
 */
@ApplicationScoped
public class ArtemisActiveMQSingletons implements JmsSingletonsIntf {

    private AtomicReference<ActiveMQConnectionFactory> connectionFactoryAtomicReference = new AtomicReference<>();

    private AtomicReference<Connection> mdbConnection = new AtomicReference<Connection>();

    private Logger logger = LoggerFactory.getLogger("JmsFactory");

    private AtomicReference<Map<String, Destination>> destinations = new AtomicReference<>(new ConcurrentHashMap<>());
    private AtomicReference<EmbeddedActiveMQ> embedded = new AtomicReference<>();

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
            getConnection().close();
            embedded.get().stop();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
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
        if(embedded.get() == null) {
            EmbeddedActiveMQ embeddedObject = new EmbeddedActiveMQ();
            try {
                embeddedObject.start();
            } catch (Exception e) {
                logger.error("Initializing Artemis", e);
                throw e;
            }
            embedded.set(embeddedObject);
        }
        if(connectionFactoryAtomicReference.get() == null) {
            ServerLocator serverLocator = ActiveMQClient.createServerLocatorWithoutHA(new TransportConfiguration(
                    InVMConnectorFactory.class.getName()));
            ActiveMQConnectionFactory cf = new ActiveMQJMSConnectionFactory(serverLocator);

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
        listener.onMessage(message);
    }
}
