package com.oneandone.ejbcdiunit.asynchmdb;

import static org.hamcrest.core.Is.is;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.SessionContextFactory;
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
@RunWith(EjbUnitRunner.class)
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
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), is(5));
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls2(), is(5));
        cdiMdbClient.sendMessageToQueue();
        asynchronousManager.once();
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls(), is(10));
        Assert.assertThat(mdbEjbInfoSingleton.getNumberOfQCalls2(), is(10));
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
}
