package com.oneandone.iocunit.ejb.jms;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.AsynchronousSimulator;
import com.oneandone.iocunit.jms.AsynchronousMessageListenerProxy;
import com.oneandone.iocunit.jms.JmsProducers;
import com.oneandone.iocunit.jms.JmsSingletonsIntf;

/**
 * Used to do the CDI-Part of JMS-Simulation.
 *
 * @author aschoerk
 */

@Singleton
public class EjbJmsMocksFactory {
    protected static ThreadLocal<Boolean> postConstructing = new ThreadLocal<>();
    @Inject
    protected Provider<JmsSingletonsIntf> jmsSingletons;
    protected ArrayList<MessageConsumer> messageConsumers = new ArrayList<>();
    protected Logger logger = LoggerFactory.getLogger("JmsMdbConnector");
    @Inject
    private AsynchronousSimulator asynchronousManager;


    @Inject
    private Instance<MessageListener> messageListeners;
    private AtomicBoolean initedMessageListeners = new AtomicBoolean(false);

    /**
     * Handle multiple creation/destroys of cdi-containers correctly. remove all in mockrunner-jms
     */
    @PreDestroy
    public void predestroy() {
        for (MessageConsumer messageConsumer : messageConsumers) {
            try {
                messageConsumer.close();
            } catch (JMSException e) {

            }
        }
        jmsSingletons.get().destroy();
    }

    /**
     * connect Mdb with the mockrunner jms
     *
     * @throws JMSException should not occur since mockrunner creates everything in main memory.
     */
    @SuppressWarnings("resource")
    public synchronized void initMessageListeners() throws JMSException {
        if(!initedMessageListeners.get()) {
            logger.info("JmsMdbConnector.postConstruct initMessageListeners start");
            for (MessageListener messageListener : messageListeners) {
                logger.info("JmsMdbConnector initMessageListeners {}", messageListener);
                Class clazz = messageListener.getClass();
                if(!clazz.isAnnotationPresent(MessageDriven.class)) {
                    clazz = clazz.getSuperclass();
                }
                MessageDriven messageDriven = (MessageDriven) clazz.getAnnotation(MessageDriven.class);
                String destinationType = null;
                Integer acknowledgeMode = null;
                String destination = null;
                String messageSelector = null;
                for (ActivationConfigProperty p : messageDriven.activationConfig()) {

                    if("destinationType".equals(p.propertyName())) {
                        destinationType = p.propertyValue();
                    }
                    else if("destination".equals(p.propertyName())) {
                        destination = JmsProducers.calculateCommonName(p.propertyValue());
                    }
                    else if("acknowledgeMode".equals(p.propertyName())) {
                        acknowledgeMode = "Auto_acknowledge".equals(p.propertyValue()) ? Session.AUTO_ACKNOWLEDGE : Session.DUPS_OK_ACKNOWLEDGE;
                    }
                    else if("messageSelector".equals(p.propertyName())) {
                        messageSelector = p.propertyValue();
                    }
                    else if("destinationType".equals(p.propertyName())) {
                        destinationType = p.propertyValue();
                    }
                }
                if(destinationType != null && destination != null) {
                    logger.info("JmsMdbConnector initMessageListeners destination: {}", destination);
                    final Connection connection = jmsSingletons.get().getConnection();
                    Session session = acknowledgeMode == null ? connection.createSession(false, Session.AUTO_ACKNOWLEDGE) : connection.createSession(false, acknowledgeMode);
                    Destination dest = null;
                    if("javax.jms.Queue".equals(destinationType)) {
                        dest = jmsSingletons.get().createQueue(destination);
                    }
                    else if("javax.jms.Topic".equals(destinationType)) {
                        dest = jmsSingletons.get().createTopic(destination);
                    }
                    final MessageConsumer messageConsumer = messageSelector == null ? session.createConsumer(dest) : session.createConsumer(dest, messageSelector);
                    messageConsumers.add(messageConsumer);
                    messageConsumer.setMessageListener(new AsynchronousMessageListenerProxy(messageListener, asynchronousManager, jmsSingletons.get()));
                }
            }
            logger.info("JmsMdbConnector.postConstruct initMessageListeners done");
        }
    }


}
