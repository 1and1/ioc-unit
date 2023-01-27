package com.oneandone.iocunit.validate;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import javax.naming.NamingException;

import org.hibernate.validator.HibernateValidator;

import com.oneandone.cdi.weldstarter.ExtensionSupport;

public class ValidateTestExtension implements Extension {

    public <T> void processAnnotatedType(@Observes AfterBeanDiscovery abd, BeanManager bm) throws NamingException {
        ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, HibernateValidator.class);
        ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, ValidationInitializer.class);
    }


}
