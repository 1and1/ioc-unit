package com.oneandone.ejbcdiunit.ejbs;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;

/**
 * @author aschoerk
 */
@MessageDriven(name = "QMdbEjb", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "myQueue1"),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "ForQMdbEjb")
})
@ApplicationScoped
public class QMdbEjb implements MessageListener  {

    @Inject
    Logger logger;

    @Inject
    private MdbEjbInfoSingleton mdbEjbInfoSingleton;

    @Resource
    MessageDrivenContext mdbContext;

    static private AtomicInteger called = new AtomicInteger();

    @Override
    public void onMessage(Message message) {
        logger.info("Message in QMdbEjb: {} this is the {}. received message", message, called.addAndGet(1));
        mdbEjbInfoSingleton.incrementNumberOfQCalls();
    }
}
