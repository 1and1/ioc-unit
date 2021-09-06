package com.oneandone.iocunit.validate;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.naming.NamingException;

import com.oneandone.cdi.weldstarter.ExtensionSupport;

public class ValidateTestExtension implements Extension {

    public <T> void processAnnotatedType(@Observes AfterBeanDiscovery abd, BeanManager bm) throws NamingException {
        ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, ValidationInitializer.class);
    }


}
