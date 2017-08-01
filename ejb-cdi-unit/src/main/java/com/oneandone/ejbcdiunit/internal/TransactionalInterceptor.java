package com.oneandone.ejbcdiunit.internal;

import javax.ejb.ApplicationException;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.RollbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.persistence.SimulatedTransactionManager;

/**
 * @author aschoerk
 */
@Interceptor
@EjbTransactional
public class TransactionalInterceptor {


    private final Logger logger =
            LoggerFactory.getLogger(TransactionalInterceptor.class);
    private SimulatedTransactionManager transactionManager = new SimulatedTransactionManager();

    private ApplicationException findApplicationException(Throwable ex) {
        // search for applicationexception
        Class<?> tmp = ex.getClass();
        ApplicationException applicationException = null;
        while (!tmp.equals(Throwable.class)) {
            applicationException = tmp.getAnnotation(ApplicationException.class);
            if (applicationException != null) {
                break;
            }
            tmp = tmp.getSuperclass();
        }
        if (applicationException != null && (tmp.equals(ex.getClass()) || applicationException.inherited())) {
            return applicationException;
        }
        return null;
    }

    /**
     * called when a EjbTransactional annotated Bean is called.
     *
     * @param ctx the Weld-Context useable to proceed
     * @return the result of the nested call
     * @throws Exception as occuring during nested call.
     */
    @AroundInvoke
    public Object manageTransaction(InvocationContext ctx)
            throws Exception {
        if (transactionManager.hasActiveTransactionInterceptor()) {

            final Class<?> declaringClass = ctx.getMethod().getDeclaringClass();
            Class<?> targetClass = getTargetClass(ctx);
            boolean beanManaged = isBeanManaged(declaringClass) || isBeanManaged(targetClass);
            if (isNotEjbClass(declaringClass) && isNotEjbClass(targetClass)
                    && ctx.getMethod().getAnnotation(EJB.class) == null
                    ) {
                return ctx.proceed();
            } else {
                if (beanManaged) {
                    transactionManager.push(TransactionAttributeType.NOT_SUPPORTED);
                } else {
                    TransactionAttribute transaction =
                            declaringClass.getAnnotation(
                                    TransactionAttribute.class);
                    TransactionAttribute transactionMethod = ctx.getMethod().getAnnotation(TransactionAttribute.class);
                    if (transactionMethod != null) {
                        transactionManager.push(transactionMethod.value());
                    } else if (transaction != null) {
                        transactionManager.push(transaction.value());
                    } else {
                        transactionManager.push(TransactionAttributeType.REQUIRED);
                    }
                }

                boolean passThroughRollbackException = true;
                try {
                    return ctx.proceed();
                } catch (Throwable ex) {
                    if (beanManaged) {
                        if (ex instanceof RuntimeException) {
                            throw new EJBException((RuntimeException) ex);
                        } else {
                            throw ex;
                        }
                    }
                    ApplicationException applicationException = findApplicationException(ex);
                    boolean doRollback =
                            applicationException != null ? applicationException.rollback() : ex instanceof RuntimeException;

                    if (doRollback) {
                        passThroughRollbackException = false;
                        transactionManager.rollback(false);
                        transactionManager.setRollbackOnly(false);
                    }

                    if (applicationException == null && ex instanceof RuntimeException) {
                        throw new EJBException((RuntimeException) ex);
                    } else {
                        throw ex;
                    }
                } finally {
                    try {
                        transactionManager.pop();
                    } catch (RollbackException rbe) {
                        if (passThroughRollbackException) {
                            throw rbe;
                        }
                    }
                }
            }
        } else {
            return ctx.proceed();
        }

    }

    private Class<?> getTargetClass(InvocationContext ctx) {
        final Object target = ctx.getTarget();
        if (target == null)
            return null;
        Class<? extends Object> res = target.getClass();
        if (res.getName().endsWith("WeldSubclass"))
            return res.getSuperclass();
        else
            return res;

    }

    private boolean isBeanManaged(Class<?> declaringClass) {
        return declaringClass != null
                && declaringClass.getAnnotation(TransactionManagement.class) != null
                && declaringClass.getAnnotation(TransactionManagement.class).value() == TransactionManagementType.BEAN;
    }

    private boolean isNotEjbClass(Class<?> declaringClass) {
        return declaringClass != null
                && declaringClass.getAnnotation(TransactionManagement.class) == null // allow transactionmanagement to
                                                                                     // be defined for non ejb-classes
                && declaringClass.getAnnotation(MessageDriven.class) == null
                && declaringClass.getAnnotation(Stateless.class) == null
                && declaringClass.getAnnotation(Stateful.class) == null
                && declaringClass.getAnnotation(Singleton.class) == null;
    }
}
