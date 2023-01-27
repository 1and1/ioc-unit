package com.oneandone.iocunitejb.ejbs;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJBContext;
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
    private EJBContext ejbContext;

    @Resource
    private MessageDrivenContext messageDrivenContext;

    public EJBContext getEjbContext() {
        return ejbContext;
    }

    public MessageDrivenContext getMessageDrivenContext() {
        return messageDrivenContext;
    }

    static private AtomicInteger called = new AtomicInteger();

    @Override
    public void onMessage(Message message) {
        logger.info("QMdbEjb: Message in QMdbEjb: {} this is the {}. received message", message, called.addAndGet(1));
        mdbEjbInfoSingleton.incrementNumberOfQCalls();
        logger.info("QMdbEjb: context is not null: {}", getEjbContext() != null);
        logger.info("QMdbEjb: context is of type EJBContext: {}", getEjbContext() instanceof MessageDrivenContext);
        logger.info("QMdbEjb: context is not null: {}", getMessageDrivenContext() != null);
        logger.info("QMdbEjb: context is of type MessageDrivenContext: {}", getMessageDrivenContext() instanceof MessageDrivenContext);
    }
}
