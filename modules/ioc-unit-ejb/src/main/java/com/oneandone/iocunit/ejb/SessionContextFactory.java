package com.oneandone.iocunit.ejb;

import javax.ejb.EJBContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

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
    @ResourceQualifier(name = "javax.ejb.EJBContext")
    EJBContext createEjbContextSimulation(InjectionPoint ip) {
        SessionContextSimulation injectedBean = new SessionContextSimulation("ejbcontext");
        injectedBean.setContainer(ip.getBean(), beanManager);
        return injectedBean;
    }

}
