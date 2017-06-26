package com.oneandone.ejbcdiunit.helpers;

import java.security.Principal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jglue.cdiunit.ProducesAlternative;

import com.oneandone.ejbcdiunit.resourcesimulators.SessionContextSimulation;

/**
 * @author aschoerk
 */
@Alternative
@ApplicationScoped
public class SessionContextFactoryAlternative {

    public static final String PRINCIPAL_NAME = "AlternativePrincipalName";

    public static class SessionContextSimulationAlternative extends SessionContextSimulation {
        public SessionContextSimulationAlternative(String preventInject) {
            super(preventInject);
        }

        /**
         * Obtain the <code>java.security.Principal</code> that identifies the caller.
         *
         * @return The <code>Principal</code> object that identifies the caller. This
         * method never returns <code>null</code>.
         * @throws IllegalStateException The Container throws the exception
         *                               if the instance is not allowed to call this method.
         * @since EJB 1.1
         */
        @Override
        public Principal getCallerPrincipal() throws IllegalStateException {
            return new Principal() {
                @Override
                public String getName() {
                    return PRINCIPAL_NAME;
                }
            };
        }
    }


    @Inject
    BeanManager beanManager;

    @Produces
    @ProducesAlternative
    SessionContextSimulation createSessionContext(InjectionPoint ip) {
        SessionContextSimulationAlternative injectedBean = new SessionContextSimulationAlternative("dummy");
        injectedBean.setContainer(ip.getBean(), beanManager);
        return injectedBean;
    }
}
