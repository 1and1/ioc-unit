package com.oneandone.iocunit.jms.rabbitmq;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;

import com.oneandone.cdi.weldstarter.ExtensionSupport;

/**
 * @author aschoerk
 */
public class RabbitMQExtension implements Extension {
    public <T> void processAfterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        TestExtensionServices.testClasses.forEach(c -> ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, c));
    }
}
