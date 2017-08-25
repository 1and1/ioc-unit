package com.oneandone.ejbcdiunit.jms;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.AsynchronousManager;
import com.oneandone.ejbcdiunit.internal.AsynchronousMessageListenerProxy;

/**
 * Used to do the CDI-Part of JMS-Simulation.
 *
 * @author aschoerk
 */

@ApplicationScoped
public class JmsMocksFactory {

    @Inject
    JmsSingletons jmsSingletons;

    private AtomicBoolean initedMessageListeners = new AtomicBoolean(false);

    private ArrayList<MessageConsumer> messageConsumers = new ArrayList<>();

    @Inject
    Instance<MessageListener> messageListeners;

    @Inject
    AsynchronousManager asynchronousManager;

    private Logger logger = LoggerFactory.getLogger("JmsMdbConnector");

    private static ThreadLocal<Boolean> postConstructing = new ThreadLocal<>();

    /**
     * initialize mockrunner to produce singleton.
     * strangely is called recursively by weld 1.1.14. Which is prevented using postConstructing.
     */
    @PostConstruct
    public void postConstruct() {
        logger.info("JmsMdbConnector.postConstruct start");
        if (postConstructing.get() != null && postConstructing.get()) {
            logger.error("JmsMdbConnector already postConstructing");
        } else {
            try {
                this.postConstructing.set(true);
                initMessageListeners(); // leads in maven to out of memory errors, need to analyze this first, so
            } catch (JMSException e) {
                throw new RuntimeException(e);
            } finally {
                this.postConstructing.set(false);
            }
        }
        // call it during test create until further notice.
        logger.info("JmsMdbConnector.postConstruct done");
    }

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
        jmsSingletons.destroy();
    }


    /**
     * connect Mdb with the mockrunner jms
     * @throws JMSException should not occur since mockrunner creates everything in main memory.
     */
    @SuppressWarnings("resource")
    private synchronized void initMessageListeners() throws JMSException {
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
                    final Connection connection = jmsSingletons.mdbConnection.get();
                    Session session = acknowledgeMode == null ? connection.createSession(false, Session.AUTO_ACKNOWLEDGE) : connection.createSession(false, acknowledgeMode);
                    Destination dest = null;
                    if ("javax.jms.Queue".equals(destinationType)) {
                        dest = jmsSingletons.getDestinationManager().createQueue(destination);
                    } else if ("javax.jms.Topic".equals(destinationType)) {
                        dest = jmsSingletons.getDestinationManager().createTopic(destination);
                    }
                    final MessageConsumer messageConsumer = messageSelector == null ? session.createConsumer(dest) : session.createConsumer(dest, messageSelector);
                    messageConsumers.add(messageConsumer);
                    messageConsumer.setMessageListener(new AsynchronousMessageListenerProxy(messageListener, asynchronousManager));
                }
            }
            logger.info("JmsMdbConnector.postConstruct initMessageListeners done");
        }
    }

    private String getResourceName(InjectionPoint ip) {
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

    private String calculateCommonName(String name) {
        int lastSlashIndex = name.lastIndexOf("/");
        if (lastSlashIndex < 0) {
            return name;
        } else {
            return name.substring(lastSlashIndex + 1);
        }
    }

    /**
     * called by CDI on each @Inject Queue. SupportEjb added this when @Resource was found
     * @param ip information about the injectionpoint can be used to analyze the annotations. - Queuename, ...
     * @return representation of the Queue to be injected
     */
    @Produces
    public Queue createQueue(InjectionPoint ip) {
        String name = getResourceName(ip);
        return jmsSingletons.getDestinationManager().createQueue(calculateCommonName(name));
    }

    /**
     * called by CDI on each @Inject Topic. SupportEjb added this when @Resource was found
     * @param ip information about the injectionpoint can be used to analyze the annotations. - Topicname, ...
     * @return representation of the Topic to be injected
     */
    @Produces
    public Topic createTopic(InjectionPoint ip) {
        String name = getResourceName(ip);
        return jmsSingletons.getDestinationManager().createTopic(calculateCommonName(name));
    }

    /**
     * creates the jms-connectionfactory which is injected anywhere during the tests.
     * @return one ConnectionFactory able to create mockrunner-jms-objects
     * @throws Exception should not occur since mockrunner uses the main memory for jms.
     */
    @Produces
    @ApplicationScoped
    public ConnectionFactory getConnectionFactory() throws Exception {
        return jmsSingletons.getConnectionFactory();
    }
}
