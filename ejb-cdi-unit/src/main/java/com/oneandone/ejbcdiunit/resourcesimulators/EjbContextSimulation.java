package com.oneandone.ejbcdiunit.resourcesimulators;

import java.security.Identity;
import java.security.Principal;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBContext;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.TimerService;
import javax.transaction.UserTransaction;

import com.oneandone.ejbcdiunit.persistence.SimulatedTransactionManager;

/**
 * @author aschoerk
 */
public abstract class EjbContextSimulation implements EJBContext {
    /**
     * Obtain the enterprise bean's remote home interface.
     *
     * @return The enterprise bean's remote home interface.
     * @throws IllegalStateException if the enterprise bean
     *                               does not have a remote home interface.
     */
    @Override
    public EJBHome getEJBHome() throws IllegalStateException {
        throw new NotImplementedException("getEJBHome not implemented in SessionContextSimulation of ejb-cdi-unit");
    }

    /**
     * Obtain the enterprise bean's local home interface.
     *
     * @return The enterprise bean's local home interface.
     * @throws IllegalStateException if the enterprise bean
     *                               does not have a local home interface.
     * @since EJB 2.0
     */
    @Override
    public EJBLocalHome getEJBLocalHome() throws IllegalStateException {
        throw new NotImplementedException("getEJBLocalHome not implemented in SessionContextSimulation of ejb-cdi-unit");
    }

    /**
     * Obtain the enterprise bean's environment properties.
     * <p>
     * <p><b>Note:</b> If the enterprise bean has no environment properties
     * this method returns an empty <code>java.util.Properties</code> object.
     * This method never returns <code>null</code>.
     *
     * @return The environment properties for the enterprise bean.
     * @deprecated Use the JNDI naming context java:comp/env to access
     * enterprise bean's environment.
     */
    @Override
    public Properties getEnvironment() {
        return new Properties();
    }

    /**
     * Obtain the <code>java.security.Identity</code> of the caller.
     * <p>
     * This method is deprecated in EJB 1.1. The Container
     * is allowed to return always <code>null</code> from this method. The enterprise
     * bean should use the <code>getCallerPrincipal</code> method instead.
     *
     * @return The <code>Identity</code> object that identifies the caller.
     * @deprecated Use Principal getCallerPrincipal() instead.
     */
    @Override
    public Identity getCallerIdentity() {
        throw new NotImplementedException("getCallerIdentity not implemented in SessionContextSimulation of ejb-cdi-unit");
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
                return "TestPrincipalName";
            }
        };
    }

    /**
     * Test if the caller has a given role.
     * <p>
     * <p>This method is deprecated in EJB 1.1. The enterprise bean
     * should use the <code>isCallerInRole(String roleName)</code> method instead.
     *
     * @param role The <code>java.security.Identity</code> of the role to be tested.
     * @return True if the caller has the specified role.
     * @deprecated Use boolean isCallerInRole(String roleName) instead.
     */
    @Override
    public boolean isCallerInRole(Identity role) {
        throw new NotImplementedException("isCallerInRole not implemented in SessionContextSimulation of ejb-cdi-unit");
    }

    /**
     * Test if the caller has a given security role.
     *
     * @param roleName The name of the security role. The role must be one of
     *                 the security roles that is defined in the deployment descriptor.
     * @return True if the caller has the specified role.
     * @throws IllegalStateException The Container throws the exception
     *                               if the instance is not allowed to call this method.
     * @since EJB 1.1
     */
    @Override
    public boolean isCallerInRole(String roleName) throws IllegalStateException {
        return true;
    }

    /**
     * Obtain the transaction demarcation interface.
     * <p>
     * Only enterprise beans with bean-managed transactions are allowed to
     * to use the <code>UserTransaction</code> interface. As entity beans must always use
     * container-managed transactions, only session beans or message-driven
     * beans with bean-managed transactions are allowed to invoke this method.
     *
     * @return The <code>UserTransaction</code> interface that the enterprise bean
     * instance can use for transaction demarcation.
     * @throws IllegalStateException The Container throws the exception
     *                               if the instance is not allowed to use the <code>UserTransaction</code> interface
     *                               (i.e. the instance is of a bean with container-managed transactions).
     */
    @Override
    public UserTransaction getUserTransaction() throws IllegalStateException {
        return new SimulatedUserTransaction();
    }

    /**
     * Mark the current transaction for rollback. The transaction will become
     * permanently marked for rollback. A transaction marked for rollback
     * can never commit.
     * <p>
     * Only enterprise beans with container-managed transactions are allowed
     * to use this method.
     *
     * @throws IllegalStateException The Container throws the exception
     *                               if the instance is not allowed to use this method (i.e. the
     *                               instance is of a bean with bean-managed transactions).
     */
    @Override
    public void setRollbackOnly() throws IllegalStateException {
        new SimulatedTransactionManager().setRollbackOnly(false);
    }

    /**
     * Test if the transaction has been marked for rollback only. An enterprise
     * bean instance can use this operation, for example, to test after an
     * exception has been caught, whether it is fruitless to continue
     * computation on behalf of the current transaction.
     * <p>
     * Only enterprise beans with container-managed transactions are allowed
     * to use this method.
     *
     * @return True if the current transaction is marked for rollback, false
     * otherwise.
     * @throws IllegalStateException The Container throws the exception
     *                               if the instance is not allowed to use this method (i.e. the
     *                               instance is of a bean with bean-managed transactions).
     */
    @Override
    public boolean getRollbackOnly() throws IllegalStateException {
        return new SimulatedTransactionManager().getRollbackOnly(false);
    }

    /**
     * Get access to the EJB Timer Service.
     *
     * @throws IllegalStateException The Container throws the exception
     *                               if the instance is not allowed to use this method (e.g. if the bean
     *                               is a stateful session bean)
     * @since EJB 2.1
     */
    @Override
    public TimerService getTimerService() throws IllegalStateException {
        throw new NotImplementedException("getTimerService not implemented in SessionContextSimulation of ejb-cdi-unit");
    }

    /**
     * Lookup a resource within the <code>java:</code> namespace.  Names referring to
     * entries within the private component namespace can be passed as
     * unqualified strings.  In that case the lookup will be relative to
     * <code>"java:comp/env/"</code>.
     * <p>
     * For example, assuming an enterprise bean defines an <code>ejb-local-ref</code>
     * with <code>ejb-ref-name</code> <code>"ejb/BarRef"</code> the following two
     * calls to <code> EJBContext.lookup</code> are equivalent :
     * <p>
     * <code>ejbContext.lookup("ejb/BarRef")</code>;
     * <code>ejbContext.lookup("java:comp/env/ejb/BarRef")</code>;
     *
     * @param name Name of the entry
     * @throws IllegalArgumentException The Container throws the exception
     *                                  if the given name does not match an entry within the component's
     *                                  environment.
     * @since EJB 3.0
     */
    @Override
    public Object lookup(String name) throws IllegalArgumentException {
        throw new NotImplementedException("lookup not implemented in SessionContextSimulation of ejb-cdi-unit");
    }

    /**
     * The <code>getContextData</code> method enables a business method, lifecycle
     * callback method, or timeout method to retrieve any interceptor/webservices context
     * associated with its invocation.
     *
     * @return the context data that interceptor context associated with this invocation.
     * If there is no context data, an empty <code>Map&#060;String,Object&#062;</code>
     * object will be returned.
     * @since EJB 3.1
     */
    @Override
    public Map<String, Object> getContextData() {
        return new java.util.HashMap<>();
    }
}
