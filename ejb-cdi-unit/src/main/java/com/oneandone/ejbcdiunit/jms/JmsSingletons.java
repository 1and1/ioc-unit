package com.oneandone.ejbcdiunit.jms;

import java.util.concurrent.atomic.AtomicReference;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.MockConnectionFactory;

/**
 * Manages the singleton used to mock JMS in CDI-Unit using mockrunner.
 *
 * @author aschoerk
 */
public class JmsSingletons {

    static AtomicReference<DestinationManager> destinationManagerAtomicReference = new AtomicReference<>();

    static AtomicReference<MockConnectionFactory> connectionFactoryAtomicReference = new AtomicReference<>();

    static AtomicReference<Connection> mdbConnection = new AtomicReference<>();

    Logger logger = LoggerFactory.getLogger("JmsFactory");

    JmsSingletons() {
        try {
            getConnectionFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * make sure a new weld-container can be created without residue
     */
    public void destroy() {
        if (connectionFactoryAtomicReference.get() != null) {
            connectionFactoryAtomicReference.get().clearConnections();
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
            MockConnectionFactory tmp = new MockConnectionFactory(getDestinationManager(), configurationManager);
            if (connectionFactoryAtomicReference.compareAndSet(null, tmp)) {
                mdbConnection.set(tmp.createConnection());
            }
        }
        return new MockConnectionFactoryExt(connectionFactoryAtomicReference.get());
    }

}
