package com.oneandone.iocunit.ejb.trainterceptors;

import java.lang.annotation.Annotation;
import java.util.List;

import jakarta.annotation.Priority;
import jakarta.ejb.ApplicationException;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Transactional;

import org.slf4j.MDC;

import com.oneandone.iocunit.ejb.ApplicationExceptionDescription;

/**
 * @author aschoerk
 */
@Interceptor
@EjbTransactional
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class TransactionalInterceptorEjb extends TransactionalInterceptorBase {
    private ApplicationException findApplicationException(Throwable ex) {
        // search for applicationexception
        Class<?> tmp = ex.getClass();
        ApplicationException applicationException = null;
        while (!tmp.equals(Throwable.class)) {
            applicationException = tmp.getAnnotation(ApplicationException.class);
            if (applicationException == null) {
                List<ApplicationExceptionDescription> ejbJarDescriptions = ejbInformationBean.getApplicationExceptionDescriptions();
                if (ejbJarDescriptions != null) {
                    for (final ApplicationExceptionDescription aed : ejbJarDescriptions) {
                        if (aed.getClassName().equals(tmp.getName())) {
                            applicationException = new ApplicationException() {
                                @Override
                                public Class<? extends Annotation> annotationType() {
                                    return ApplicationException.class;
                                }

                                @Override
                                public boolean inherited() {
                                    return aed.isInherited();
                                }

                                @Override
                                public boolean rollback() {
                                    return aed.isRollback();
                                }
                            };
                            break;
                        }
                    }
                }

            }
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
            if (isNotTransactionalClass(declaringClass) && isNotTransactionalClass(targetClass)
                && ctx.getMethod().getAnnotation(EJB.class) == null
                && ctx.getMethod().getAnnotation(Transactional.class) == null

            ) {
                return ctx.proceed();
            } else {
                incLevel();
                TransactionAttributeType savedLastTransactionAttributeType = lastTransactionAttributeType.get();
                TransactionAttributeType toPush = null;
                if (beanManaged) {
                    toPush = TransactionAttributeType.NOT_SUPPORTED;
                } else {
                    TransactionAttribute ejbTransaction = findAnnotation(declaringClass, TransactionAttribute.class);
                    TransactionAttribute ejbTransactionMethod = ctx.getMethod().getAnnotation(TransactionAttribute.class);

                    if (ejbTransactionMethod != null) {
                        if (!isEjb(declaringClass)  && !isEjb(targetClass)) {
                            logger.error("Trying @TransactionAttribute on non Ejb-Class {}", declaringClass.getName(), targetClass.getName());
                        } else {
                            toPush = ejbTransactionMethod.value();
                        }
                    } else if (ejbTransaction != null) {
                        if (!isEjb(declaringClass)  && !isEjb(targetClass)) {
                            logger.error("Trying TransactionAttribute on non Ejb-Class {}", declaringClass.getName(), targetClass.getName());
                        } else {
                            toPush = ejbTransaction.value();
                        }
                    } else {
                        toPush = TransactionAttributeType.REQUIRED;
                    }
                }
                if (toPush == null) {
                    return ctx.proceed();
                }
                transactionManager.push(toPush);
                lastTransactionAttributeType.set(toPush);

                boolean passThroughRollbackException = true;
                try {
                    logger.debug("Thread {} L{} changing  from {} to {} xid: {} in {}.{}",
                            Thread.currentThread().getId(), getLevel(),
                            savedLastTransactionAttributeType == null ? "undefined" : savedLastTransactionAttributeType,
                            toPush, MDC.get("XID"), declaringClass.getSimpleName(), ctx.getMethod().getName());
                    return ctx.proceed();
                } catch (Throwable ex) {
                    logger.debug("Thread {} L{} Exception {} in {} xid: {} in {}.{}",
                            Thread.currentThread().getId(), getLevel(),
                            ex.getClass().getSimpleName(), toPush, MDC.get("XID"), declaringClass.getSimpleName(),
                            ctx.getMethod().getName());
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
                    logger.debug("Thread {} L{} finally   in {} xid: {} in {}.{}",
                            Thread.currentThread().getId(), getLevel(), toPush, MDC.get("XID"), declaringClass.getSimpleName(),
                            ctx.getMethod().getName());
                    try {
                        transactionManager.pop();
                    } catch (RollbackException rbe) {
                        if (passThroughRollbackException) {
                            throw rbe;
                        }
                    } finally {
                        logger.debug("Thread {} L{} done      {} back to {} xid: {} in {}.{}",
                                Thread.currentThread().getId(), getLevel(), toPush,
                                savedLastTransactionAttributeType == null ? "undefined" : savedLastTransactionAttributeType,
                                MDC.get("XID"), declaringClass.getSimpleName(), ctx.getMethod().getName());
                    }
                    lastTransactionAttributeType.set(savedLastTransactionAttributeType);
                    decLevel();
                }
            }
        } else {
            return ctx.proceed();
        }

    }

    protected boolean isEjb(Class<?> declaringClass) {
        return declaringClass != null
               // be defined for non ejb-classes
               || declaringClass.getAnnotation(MessageDriven.class) != null
               || declaringClass.getAnnotation(Stateless.class) != null
               || declaringClass.getAnnotation(Stateful.class) != null
               || declaringClass.getAnnotation(Singleton.class) != null;
    }

}
