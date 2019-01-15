package com.oneandone.ejbcdiunit5.asynchmdb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.ejb.AsynchronousManager;
import com.oneandone.iocunit.ejb.SessionContextFactory;
import com.oneandone.ejbcdiunit.ejbs.CdiMdbClient;
import com.oneandone.ejbcdiunit.ejbs.MdbEjbInfoSingleton;
import com.oneandone.ejbcdiunit.ejbs.QMdbEjb;
import com.oneandone.ejbcdiunit.ejbs.QMdbEjb2;
import com.oneandone.ejbcdiunit.ejbs.SingletonMdbClient;
import com.oneandone.ejbcdiunit.ejbs.TMdbEjb;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@SutClasses({ QMdbEjb.class, QMdbEjb2.class, TMdbEjb.class })
@SutPackages(CdiMdbClient.class)
@TestClasses({ SessionContextFactory.class, LoggerGenerator.class })
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
