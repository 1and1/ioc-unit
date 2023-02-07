package com.oneandone.iocunit.ejb;

import jakarta.ejb.EJBContext;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import com.oneandone.iocunit.ejb.resourcesimulators.SessionContextSimulation;
import com.oneandone.iocunit.resource.ResourceQualifier;

/**
 * @author aschoerk
 */
@SupportEjbExtended
public class SessionContextFactory {

    @Inject
    BeanManager beanManager;

    @Produces
    SessionContextSimulation createSessionContext(InjectionPoint ip) {
        SessionContextSimulation injectedBean = new SessionContextSimulation("dummy");
        injectedBean.setContainer(ip.getBean(), beanManager);
        return injectedBean;
    }

    @Produces
    @ResourceQualifier(name = "jakarta.ejb.EJBContext")
    EJBContext createEjbContextSimulation(InjectionPoint ip) {
        SessionContextSimulation injectedBean = new SessionContextSimulation("ejbcontext");
        injectedBean.setContainer(ip.getBean(), beanManager);
        return injectedBean;
    }

}
