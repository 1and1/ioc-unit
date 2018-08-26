package com.oneandone.ejbcdiunit5.helpers;

import org.jglue.cdiunit.ProducesAlternative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;
import java.security.Identity;
import java.security.Principal;
import java.util.Map;
import java.util.Properties;

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
            public MessageContext getMessageContext() throws IllegalStateException {
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
            public Properties getEnvironment() {
                return null;
            }


            @Override
            public Identity getCallerIdentity() {
                return null;
            }


            @Override
            public Principal getCallerPrincipal() throws IllegalStateException {
                return null;
            }


            @Override
            public boolean isCallerInRole(Identity role) {
                return false;
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
