package com.oneandone.iocunitejb.helpers;

import java.security.Principal;
import java.util.Map;
import java.util.Properties;

import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.EJBLocalObject;
import jakarta.ejb.EJBObject;
import jakarta.ejb.SessionContext;
import jakarta.ejb.TimerService;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;

/**
 * @author aschoerk
 */
@Alternative
public class TestResources {
    static Logger logger = LoggerFactory.getLogger("logger");

    @Inject
    J2eeSimTest1Factory entityManagerFactory;

    @ProducesAlternative
    @Produces
    EntityManager newEm() {
        return entityManagerFactory.produceEntityManager();
    }


    @Produces
    SessionContext newSessionContext() {
        return new SessionContext() {

            @Override
            public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
                return null;
            }


            @Override
            public EJBObject getEJBObject() throws IllegalStateException {
                return null;
            }

            @Override
            public <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException {
                return null;
            }


            @Override
            public Class getInvokedBusinessInterface() throws IllegalStateException {
                return null;
            }


            @Override
            public boolean wasCancelCalled() throws IllegalStateException {
                return false;
            }


            @Override
            public EJBHome getEJBHome() throws IllegalStateException {
                return null;
            }


            @Override
            public EJBLocalHome getEJBLocalHome() throws IllegalStateException {
                return null;
            }

            @Override
            public Principal getCallerPrincipal() throws IllegalStateException {
                return null;
            }

            @Override
            public boolean isCallerInRole(String roleName) throws IllegalStateException {
                return false;
            }


            @Override
            public UserTransaction getUserTransaction() throws IllegalStateException {
                return null;
            }


            @Override
            public void setRollbackOnly() throws IllegalStateException {

            }


            @Override
            public boolean getRollbackOnly() throws IllegalStateException {
                return false;
            }


            @Override
            public TimerService getTimerService() throws IllegalStateException {
                return null;
            }


            @Override
            public Object lookup(String name) throws IllegalArgumentException {
                return null;
            }


            @Override
            public Map<String, Object> getContextData() {
                return null;
            }
        };
    }
}
