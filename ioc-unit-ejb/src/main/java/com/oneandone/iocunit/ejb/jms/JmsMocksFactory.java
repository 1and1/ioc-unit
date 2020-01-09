package com.oneandone.iocunit.ejb.jms;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.ejb.AsynchronousManager;
import com.oneandone.iocunit.ejb.AsynchronousMessageListenerProxy;

/**
 * Used to do the CDI-Part of JMS-Simulation.
 *
 * @author aschoerk
 */

@Singleton
public class JmsMocksFactory {

    @Inject
    private Provider<JmsSingletonsIntf> jmsSingletons;

    private AtomicBoolean initedMessageListeners = new AtomicBoolean(false);

    private ArrayList<MessageConsumer> messageConsumers = new ArrayList<>();

    @Inject
    private Instance<MessageListener> messageListeners;

    @Inject
    private AsynchronousManager asynchronousManager;

    private Logger logger = LoggerFactory.getLogger("JmsMdbConnector");

    private static ThreadLocal<Boolean> postConstructing = new ThreadLocal<>();

    /**
     * Handle multiple creation/destroys of cdi-containers correctly. remove all in mockrunner-jms
     */
    @PreDestroy
    public void predestroy() {
        for (MessageConsumer messageConsumer: messageConsumers) {
            try {
                messageConsumer.close();
            } catch (JMSException e) {

            }
        }
        jmsSingletons.get().destroy();
    }


    /**
     * connect Mdb with the mockrunner jms
     * @throws JMSException should not occur since mockrunner creates everything in main memory.
     */
    @SuppressWarnings("resource")
    public synchronized void initMessageListeners() throws JMSException {
        if (!initedMessageListeners.get()) {
            logger.info("JmsMdbConnector.postConstruct initMessageListeners start");
            for (MessageListener messageListener : messageListeners) {
                logger.info("JmsMdbConnector initMessageListeners {}", messageListener);
                Class clazz = messageListener.getClass();
                if (!clazz.isAnnotationPresent(MessageDriven.class)) {
                    clazz = clazz.getSuperclass();
                }
                MessageDriven messageDriven = (MessageDriven) clazz.getAnnotation(MessageDriven.class);
                String destinationType = null;
                Integer acknowledgeMode = null;
                String destination = null;
                String messageSelector = null;
                for (ActivationConfigProperty p : messageDriven.activationConfig()) {

                    if ("destinationType".equals(p.propertyName())) {
                        destinationType = p.propertyValue();
                    } else if ("destination".equals(p.propertyName())) {
                        destination = calculateCommonName(p.propertyValue());
                    } else if ("acknowledgeMode".equals(p.propertyName())) {
                        acknowledgeMode = "Auto_acknowledge".equals(p.propertyValue()) ? Session.AUTO_ACKNOWLEDGE : Session.DUPS_OK_ACKNOWLEDGE;
                    } else if ("messageSelector".equals(p.propertyName())) {
                        messageSelector = p.propertyValue();
                    } else if ("destinationType".equals(p.propertyName())) {
                        destinationType = p.propertyValue();
                    }
                }
                if (destinationType != null && destination != null) {
                    logger.info("JmsMdbConnector initMessageListeners destination: {}", destination);
                    final Connection connection = jmsSingletons.get().getConnection();
                    Session session = acknowledgeMode == null ? connection.createSession(false, Session.AUTO_ACKNOWLEDGE) : connection.createSession(false, acknowledgeMode);
                    Destination dest = null;
                    if ("javax.jms.Queue".equals(destinationType)) {
                        dest = jmsSingletons.get().createQueue(destination);
                    } else if ("javax.jms.Topic".equals(destinationType)) {
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


    /**
     * creates the jms-connectionfactory which is injected anywhere during the tests.
     * @return one ConnectionFactory able to create mockrunner-jms-objects
     * @throws Exception should not occur since mockrunner uses the main memory for jms.
     */
    @Produces
    @ApplicationScoped
    public ConnectionFactory getConnectionFactory() throws Exception {
        return jmsSingletons.get().getConnectionFactory();
    }



    static String getResourceName(InjectionPoint ip) {
        Resource resourceAnnotation = ip.getAnnotated().getAnnotation(Resource.class);
        String name = resourceAnnotation.mappedName();
        if (name.trim().isEmpty()) {
            name = resourceAnnotation.lookup();
            if (name.trim().isEmpty())  {
                name = "dummyName";
            }
        }
        return name;
    }

    static String calculateCommonName(String name) {
        int lastSlashIndex = name.lastIndexOf("/");
        if (lastSlashIndex < 0) {
            return name;
        } else {
            return name.substring(lastSlashIndex + 1);
        }
    }

  }
