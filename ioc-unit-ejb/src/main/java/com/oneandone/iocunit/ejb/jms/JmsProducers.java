package com.oneandone.iocunit.ejb.jms;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class JmsProducers {
    @Inject
    private JmsSingletons jmsSingletons;

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
