package com.oneandone.iocunitejb.ejbs;

import java.util.Random;
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
@MessageDriven(name = "TMdbEjb", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
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
