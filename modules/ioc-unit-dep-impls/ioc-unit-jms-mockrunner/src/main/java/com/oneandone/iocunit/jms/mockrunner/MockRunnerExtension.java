package com.oneandone.iocunit.jms.mockrunner;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import com.oneandone.cdi.weldstarter.ExtensionSupport;

/**
 * @author aschoerk
 */
public class MockRunnerExtension implements Extension {
    public <T> void processAfterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        TestExtensionServices.testClasses.forEach(c -> ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, c));
    }
}
