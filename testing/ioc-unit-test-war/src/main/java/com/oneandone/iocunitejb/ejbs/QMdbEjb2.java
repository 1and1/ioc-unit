package com.oneandone.iocunitejb.ejbs;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.MessageDrivenContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

import org.slf4j.Logger;

/**
 * @author aschoerk
 */
@MessageDriven(name = "QMdbEjb2", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "myQueue1"),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "ForQMdbEjb2")
})
@ApplicationScoped
public class QMdbEjb2 implements MessageListener  {

    @Inject
    Logger logger;

    @Inject
    private MdbEjbInfoSingleton mdbEjbInfoSingleton;

    @Resource
    MessageDrivenContext mdbContext;

    static private AtomicInteger called = new AtomicInteger();

    @Override
    public void onMessage(Message message) {
        logger.info("Message in QMdbEjb2: {} this is the {}. received message", message, called.addAndGet(1));
        mdbEjbInfoSingleton.incrementNumberOfQCalls2();
    }
}
