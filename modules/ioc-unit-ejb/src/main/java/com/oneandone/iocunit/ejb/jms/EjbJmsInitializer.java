package com.oneandone.iocunit.ejb.jms;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.jms.JmsProducersException;

/**
 * @author aschoerk
 */
@Singleton
public class EjbJmsInitializer {
    @Inject
    Provider<EjbJmsMocksFactory> jmsMocksFactory;
    private Logger logger = LoggerFactory.getLogger("JmsInitializer");

    @PostConstruct
    public void postConstruct() {
        try {
            jmsMocksFactory.get().initMessageListeners();
        } catch (JMSException e) {
            logger.error(e.getMessage());
        } catch (JmsProducersException e) {
            logger.error(e.getMessage());
        }
    }

    public void dummyCall() {
        logger.info("DummyCall of JmsInitializer done");
    }

}
