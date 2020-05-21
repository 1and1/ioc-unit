package com.oneandone.iocunit.ejb.jms;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
@Singleton
public class JmsInitializer {
    @Inject
    Provider<JmsMocksFactory> jmsMocksFactory;
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
