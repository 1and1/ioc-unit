package com.oneandone.iocunitejb.asynchmdb;

import static org.hamcrest.core.Is.is;

import jakarta.inject.Inject;
import jakarta.jms.JMSException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.ejb.AsynchronousManager;
import com.oneandone.iocunit.ejb.SessionContextFactory;
import com.oneandone.iocunitejb.ejbs.CdiMdbClient;
import com.oneandone.iocunitejb.ejbs.MdbEjbInfoSingleton;
import com.oneandone.iocunitejb.ejbs.QMdbEjb;
import com.oneandone.iocunitejb.ejbs.QMdbEjb2;
import com.oneandone.iocunitejb.ejbs.SingletonMdbClient;
import com.oneandone.iocunitejb.ejbs.TMdbEjb;
import com.oneandone.iocunitejb.helpers.LoggerGenerator;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
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
        dispatchAndCheck(5);
        cdiMdbClient.sendMessageToQueue();
        dispatchAndCheck(10);
    }

    @Test
    public void testTopic() throws JMSException {
        // jmsFactory.initMessageListeners();
        singletonMdbClient.sendMessageToTopic();
        asynchronousManager.once();
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfTCalls(), is(10));
        cdiMdbClient.sendMessageToTopic();
        asynchronousManager.once();
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfTCalls(), is(20));
    }

    @Test
    public void testQueuesViaContext() throws JMSException {
        // jmsFactory.initMessageListeners();
        singletonMdbClient.sendMessageViaContextToQueue();
        dispatchAndCheck(5);
        cdiMdbClient.sendMessageViaContextToQueue();
        dispatchAndCheck(10);
    }

    @Test
    public void testQueuesViaInjectedContext() throws JMSException {
        // jmsFactory.initMessageListeners();
        singletonMdbClient.sendMessageViaInjectedContextToQueue();
        dispatchAndCheck(5);
        cdiMdbClient.sendMessageViaInjectedContextToQueue();
        dispatchAndCheck(10);
    }

    private void dispatchAndCheck(final int i) {
        asynchronousManager.once();
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), is(i));
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls2(), is(i));
    }

}
