package com.oneandone.ejbcdiunit5.asynchmdb;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.SessionContextFactory;
import com.oneandone.ejbcdiunit.ejbs.*;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.jms.JMSException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({  SessionContextFactory.class, LoggerGenerator.class,
        QMdbEjb.class, QMdbEjb2.class, TMdbEjb.class })
public class TestMdb {
    @Inject
    SingletonMdbClient singletonMdbClient;

    @Inject
    CdiMdbClient cdiMdbClient;

    @Inject
    MdbEjbInfoSingleton mdbEjbInfoSingleton;

    @Inject
    AsynchronousManager asynchronousManager;

    @Test
    public void testQueues() throws JMSException {
        // jmsFactory.initMessageListeners();
        singletonMdbClient.sendMessageToQueue();
        asynchronousManager.once();
        assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), is(5));
        assertThat(mdbEjbInfoSingleton.getNumberOfQCalls2(), is(5));
        cdiMdbClient.sendMessageToQueue();
        asynchronousManager.once();
        assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), is(10));
        assertThat(mdbEjbInfoSingleton.getNumberOfQCalls2(), is(10));
    }

    @Test
    public void testTopic() throws JMSException {
        // jmsFactory.initMessageListeners();
        singletonMdbClient.sendMessageToTopic();
        asynchronousManager.once();
        assertThat(mdbEjbInfoSingleton.getNumberOfTCalls(), is(10));
        cdiMdbClient.sendMessageToTopic();
        asynchronousManager.once();
        assertThat(mdbEjbInfoSingleton.getNumberOfTCalls(), is(20));
    }
}
