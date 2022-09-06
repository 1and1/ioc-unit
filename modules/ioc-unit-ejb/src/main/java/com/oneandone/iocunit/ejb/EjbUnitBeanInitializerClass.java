package com.oneandone.iocunit.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * @author aschoerk
 */
@Dependent
public class EjbUnitBeanInitializerClass {
    @Inject
    EjbExtensionExtended ejbExtensionExtended;

    @Inject
    BeanManager bm;

    @Inject
    AsynchronousManager asynchronousManager;

    Logger logger = null;
    private CreationalContexts creationalContexts;

    Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger("LateInitializerClass");
        }
        return logger;
    }



    @PostConstruct
    public void init() {
        List<Class<?>> startups = ejbExtensionExtended.getStartupSingletons();
        if (!startups.isEmpty()) {
            creationalContexts = new CreationalContexts(bm);
            for (Class<?> clazz : startups) {
                creationalContexts.create(clazz, ApplicationScoped.class);
            }
        }
        asynchronousManager.addTimerMethods();
    }

    @PreDestroy
    private void preDestroy() {
        if (creationalContexts != null) {
            try {
                creationalContexts.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
