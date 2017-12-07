package com.oneandone.ejbcdiunit.ejbs;

import java.util.Random;
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
@MessageDriven(name = "TMdbEjb", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "myTopic")
})
@ApplicationScoped
public class TMdbEjb implements MessageListener  {

    @Inject
    Logger logger;

    @Inject
    private MdbEjbInfoSingleton mdbEjbInfoSingleton;

    @Resource
    MessageDrivenContext mdbContext;

    private static AtomicInteger called = new AtomicInteger();

    @Override
    public void onMessage(Message message) {
        try {
            Thread.sleep(100 * new Random().nextInt(50));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Message in TMdbEjb: {} this is the {}. received message", message, called.addAndGet(1));
        mdbEjbInfoSingleton.incrementNumberOfTCalls();
    }
}
