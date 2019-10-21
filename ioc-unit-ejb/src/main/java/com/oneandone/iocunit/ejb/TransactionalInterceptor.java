package com.oneandone.iocunit.ejb;

import java.lang.annotation.Annotation;
import java.util.List;

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
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.RollbackException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.oneandone.iocunit.ejb.persistence.SimulatedTransactionManager;

/**
 * @author aschoerk
 */
@Interceptor
@EjbTransactional
public class TransactionalInterceptor {

    private final Logger logger =
            LoggerFactory.getLogger(TransactionalInterceptor.class);
    private SimulatedTransactionManager transactionManager = new SimulatedTransactionManager();

    static ThreadLocal<TransactionAttributeType> lastTransactionAttributeType = new ThreadLocal<>();
    static ThreadLocal<Integer> level = new ThreadLocal<>();

    static public int getLevel() {
        Integer actLevel = level.get();
        if (actLevel == null) {
            level.set(0);
            actLevel = 0;
        }
        return actLevel;
    }

    static public void incLevel() {
        level.set(getLevel() + 1);
    }

    static public void decLevel() {
        level.set(getLevel() - 1);
    }

    @Inject
    private EjbInformationBean ejbInformationBean;

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
                    Transactional jtaTransactional = findAnnotation(declaringClass, Transactional.class);
                    Transactional jtaTransactionalMethod = ctx.getMethod().getAnnotation(Transactional.class);

                    if (ejbTransactionMethod != null) {
                        if (!isEjb(declaringClass)  && !isEjb(targetClass)) {
                            logger.error("Trying @TransactionAttribute on non Ejb-Class {}", declaringClass.getName(), targetClass.getName());
                        } else {
                            toPush = ejbTransactionMethod.value();
                        }
                    } else if (jtaTransactionalMethod != null) {
                        if (isEjb(declaringClass)  || isEjb(targetClass))
                            logger.error("Trying @Transactional on Ejb-Class {}",declaringClass.getName(), targetClass.getName());
                        toPush = mapJTA2Ejb(jtaTransactionalMethod.value());
                    } else if (ejbTransaction != null) {
                        if (!isEjb(declaringClass)  && !isEjb(targetClass)) {
                            logger.error("Trying TransactionAttribute on non Ejb-Class {}", declaringClass.getName(), targetClass.getName());
                        } else {
                            toPush = ejbTransaction.value();
                        }
                    } else if (jtaTransactional != null) {
                        if (isEjb(declaringClass)  || isEjb(targetClass))
                            logger.error("Trying @Transactional on Ejb-Class {}",declaringClass.getName(), targetClass.getName());
                        toPush = mapJTA2Ejb(jtaTransactional.value());
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

    private TransactionAttributeType mapJTA2Ejb(final Transactional.TxType value) {
        if (value == null)
            return TransactionAttributeType.REQUIRED;

        switch (value) {
            case MANDATORY: return TransactionAttributeType.MANDATORY;
            case NEVER: return TransactionAttributeType.NEVER;
            case NOT_SUPPORTED: return TransactionAttributeType.NOT_SUPPORTED;
            case SUPPORTS: return TransactionAttributeType.SUPPORTS;
            case REQUIRED: return TransactionAttributeType.REQUIRED;
            case REQUIRES_NEW: return TransactionAttributeType.REQUIRES_NEW;
            default: throw new RuntimeException("Invalid Transactional.TxType value: " + value);
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

    <T extends Annotation> T findAnnotation(Class<?> declaringClass, Class<T> annotationType) {
        if (declaringClass == null || declaringClass.equals(Object.class))
            return null;
        T annotation = declaringClass.getAnnotation(annotationType);
        if (annotation == null) {
            return findAnnotation(declaringClass.getSuperclass(), annotationType);
        } else {
            return annotation;
        }
    }

    private boolean isBeanManaged(Class<?> declaringClass) {
        TransactionManagement annotation = findAnnotation(declaringClass, TransactionManagement.class);
        if (annotation == null)
            return false;
        else
            return annotation.value() == TransactionManagementType.BEAN;
    }

    private boolean isNotTransactionalClass(Class<?> declaringClass) {
        if (declaringClass.equals(Object.class))
            return true;
        TransactionAttribute tmAnnotation = findAnnotation(declaringClass, TransactionAttribute.class);
        if (tmAnnotation != null)
            return false;
        else {
            boolean result = declaringClass != null
                             && declaringClass.getAnnotation(TransactionManagement.class) == null // allow transactionmanagement to
                             && declaringClass.getAnnotation(Transactional.class) == null // allow transactionmanagement to
                             // be defined for non ejb-classes
                             && declaringClass.getAnnotation(MessageDriven.class) == null
                             && declaringClass.getAnnotation(Stateless.class) == null
                             && declaringClass.getAnnotation(Stateful.class) == null
                             && declaringClass.getAnnotation(Singleton.class) == null;
            return result;
        }
    }

    private boolean isEjb(Class<?> declaringClass) {
        return declaringClass != null
                         // be defined for non ejb-classes
                         || declaringClass.getAnnotation(MessageDriven.class) != null
                         || declaringClass.getAnnotation(Stateless.class) != null
                         || declaringClass.getAnnotation(Stateful.class) != null
                         || declaringClass.getAnnotation(Singleton.class) != null;
    }
}
