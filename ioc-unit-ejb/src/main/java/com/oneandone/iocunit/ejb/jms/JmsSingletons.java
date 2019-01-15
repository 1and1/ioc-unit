package com.oneandone.iocunit.ejb.jms;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.melowe.jms2.compat.Jms2ConnectionFactory;
import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.MockConnectionFactory;

/**
 * Manages the singleton used to mock JMS in CDI-Unit using mockrunner.
 *
 * @author aschoerk
 */
@ApplicationScoped
public class JmsSingletons {

    private AtomicReference<DestinationManager> destinationManagerAtomicReference = new AtomicReference<>();

    private AtomicReference<Jms2ConnectionFactory> connectionFactoryAtomicReference = new AtomicReference<>();

    private AtomicReference<MockConnectionFactory> mockConnectionFactoryAtomicReference = new AtomicReference<>();

    private AtomicReference<Connection> mdbConnection = new AtomicReference<>();

    Logger logger = LoggerFactory.getLogger("JmsFactory");

    Connection getConnection() {
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
    public void destroy() {
        if (connectionFactoryAtomicReference.get() != null) {
            mockConnectionFactoryAtomicReference.get().clearConnections();
            mockConnectionFactoryAtomicReference = new AtomicReference<>();
            connectionFactoryAtomicReference = new AtomicReference<>();
        }
        destinationManagerAtomicReference = new AtomicReference<>();
        mdbConnection = new AtomicReference<>();
    }

    DestinationManager getDestinationManager() {
        if (destinationManagerAtomicReference.get() == null) {
            DestinationManager tmp = new DestinationManager();
            destinationManagerAtomicReference.compareAndSet(null, tmp);
        }
        return destinationManagerAtomicReference.get();
    }

    /**
     * creates the jms-connectionfactory which is injected anywhere during the tests.
     * @return one ConnectionFactory able to create mockrunner-jms-objects
     * @throws Exception should not occur since mockrunner uses the main memory for jms.
     */
    public ConnectionFactory getConnectionFactory() throws Exception {
        if (connectionFactoryAtomicReference.get() == null) {
            final ConfigurationManager configurationManager = new ConfigurationManager();
            configurationManager.setDoCloneOnSend(true);
            final MockConnectionFactory mockConnectionFactory = new MockConnectionFactory(getDestinationManager(), configurationManager);
            Jms2ConnectionFactory tmp = new Jms2ConnectionFactory(mockConnectionFactory);
            if (connectionFactoryAtomicReference.compareAndSet(null, tmp)) {
                mockConnectionFactoryAtomicReference.compareAndSet(null, mockConnectionFactory);
                mdbConnection.set(tmp.createConnection());
            }
        }
        return connectionFactoryAtomicReference.get();
    }

}
