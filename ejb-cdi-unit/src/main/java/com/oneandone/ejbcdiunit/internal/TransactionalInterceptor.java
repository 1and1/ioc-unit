package com.oneandone.ejbcdiunit.internal;

import javax.ejb.EJB;
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


    private SimulatedTransactionManager transactionManager = new SimulatedTransactionManager();

    private final Logger logger =
            LoggerFactory.getLogger(TransactionalInterceptor.class);


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
            boolean beanManaged = (declaringClass.getAnnotation(TransactionManagement.class) != null
                    && declaringClass.getAnnotation(TransactionManagement.class).value() == TransactionManagementType.BEAN);
            if (declaringClass.getAnnotation(MessageDriven.class) == null
                    && declaringClass.getAnnotation(Stateless.class) == null
                    && declaringClass.getAnnotation(Stateful.class) == null
                    && declaringClass.getAnnotation(Singleton.class) == null
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
                } catch (RuntimeException ex) {
                    passThroughRollbackException = false;
                    transactionManager.rollback(false);
                    throw ex;
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
}
