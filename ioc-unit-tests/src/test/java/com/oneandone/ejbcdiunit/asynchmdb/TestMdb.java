package com.oneandone.ejbcdiunit.asynchmdb;

import static org.hamcrest.core.Is.is;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.cdi.tester.ejb.AsynchronousManager;
import com.oneandone.cdi.tester.ejb.SessionContextFactory;
import com.oneandone.ejbcdiunit.ejbs.CdiMdbClient;
import com.oneandone.ejbcdiunit.ejbs.MdbEjbInfoSingleton;
import com.oneandone.ejbcdiunit.ejbs.QMdbEjb;
import com.oneandone.ejbcdiunit.ejbs.QMdbEjb2;
import com.oneandone.ejbcdiunit.ejbs.SingletonMdbClient;
import com.oneandone.ejbcdiunit.ejbs.TMdbEjb;
import com.oneandone.ejbcdiunit.helpers.LoggerGenerator;

/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
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
