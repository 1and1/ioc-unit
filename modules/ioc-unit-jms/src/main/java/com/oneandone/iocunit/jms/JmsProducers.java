package com.oneandone.iocunit.jms;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @author aschoerk
 */
@Singleton
public class JmsProducers {
    @Inject
    private Provider<JmsSingletonsIntf> jmsSingletons;


    static public String getResourceName(InjectionPoint ip) {
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

    private JmsSingletonsIntf getJmsSingletonsIntf() {
        try {
            return jmsSingletons.get();
        } catch (UnsatisfiedResolutionException e) {
            throw new JmsProducersException("No Jms Provider found, add one of ioc-unit-jms-(activemq|rabbitmq|mockrunner) to pom.xml");
        }
    }

    static public String calculateCommonName(String name) {
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
        return getJmsSingletonsIntf().createQueue(calculateCommonName(name));
    }

    /**
     * called by CDI on each @Inject Topic. SupportEjb added this when @Resource was found
     * @param ip information about the injectionpoint can be used to analyze the annotations. - Topicname, ...
     * @return representation of the Topic to be injected
     */
    @Produces
    public Topic createTopic(InjectionPoint ip) {
        String name = getResourceName(ip);
        return getJmsSingletonsIntf().createTopic(calculateCommonName(name));
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
}
