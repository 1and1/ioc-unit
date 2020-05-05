package com.oneandone.iocunit.ejb.jms;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @author aschoerk
 */
@Singleton
public class JmsProducers {
    @Inject
    private Provider<JmsSingletonsIntf> jmsSingletons;


    /**
     * called by CDI on each @Inject Queue. SupportEjb added this when @Resource was found
     * @param ip information about the injectionpoint can be used to analyze the annotations. - Queuename, ...
     * @return representation of the Queue to be injected
     */
    @Produces
    public Queue createQueue(InjectionPoint ip) {
        String name = JmsMocksFactory.getResourceName(ip);
        return getJmsSingletonsIntf().createQueue(JmsMocksFactory.calculateCommonName(name));
    }

    private JmsSingletonsIntf getJmsSingletonsIntf() {
        try {
            return jmsSingletons.get();
        } catch (UnsatisfiedResolutionException e) {
            throw new JmsProducersException("No Jms Provider found, add one of ioc-unit-jms-(activemq|rabbitmq|mockrunner) to pom.xml");
        }
    }

    /**
     * called by CDI on each @Inject Topic. SupportEjb added this when @Resource was found
     * @param ip information about the injectionpoint can be used to analyze the annotations. - Topicname, ...
     * @return representation of the Topic to be injected
     */
    @Produces
    public Topic createTopic(InjectionPoint ip) {
        String name = JmsMocksFactory.getResourceName(ip);
        return getJmsSingletonsIntf().createTopic(JmsMocksFactory.calculateCommonName(name));
    }


}
