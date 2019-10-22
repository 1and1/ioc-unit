package com.oneandone.iocunit.ejb.trainterceptors;

import java.lang.annotation.Annotation;

import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Stereotype;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.transaction.RollbackException;
import javax.transaction.Transactional;

import org.slf4j.MDC;

/**
 * @author aschoerk
 */
public class TransactionalInterceptorJtaBase extends TransactionalInterceptorBase {
    @AroundInvoke
    public Object manageTransaction(InvocationContext ctx) throws Exception {
        if (transactionManager.hasActiveTransactionInterceptor()) {
            final Class<?> declaringClass = ctx.getMethod().getDeclaringClass();
            Class<?> targetClass = getTargetClass(ctx);

            incLevel();
            TransactionAttributeType savedLastTransactionAttributeType = lastTransactionAttributeType.get();
            TransactionAttributeType toPush = null;

            Transactional jtaTransactional = findTransactionalAnnotation(ctx, declaringClass);

            if (jtaTransactional != null) {
                toPush = mapJTA2Ejb(jtaTransactional.value());
            } else {
               throw new RuntimeException("Did not find Transactional-Annotation to push");
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

                boolean doRollback =
                        doRollback(ex,jtaTransactional);

                if (doRollback) {
                    passThroughRollbackException = false;
                    transactionManager.rollback(false);
                    transactionManager.setRollbackOnly(false);
                }

                throw ex;

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

        } else {
            return ctx.proceed();
        }
    }

    private Transactional findTransactionalAnnotation(InvocationContext ctx, Class declaringClass) {
        Transactional jtaTransactional = ctx.getMethod().getAnnotation(Transactional.class);
        if (jtaTransactional == null) {
            for (Annotation a: ctx.getMethod().getAnnotations()) {
                if (a.annotationType().getAnnotation(Stereotype.class) != null) {
                    jtaTransactional = a.annotationType().getAnnotation(Transactional.class);
                }
            }
        }
        if (jtaTransactional == null) {
            jtaTransactional = findAnnotation(declaringClass, Transactional.class);
            if (jtaTransactional == null) {
                for (Annotation a: declaringClass.getAnnotations()) {
                    if (a.annotationType().getAnnotation(Stereotype.class) != null) {
                        jtaTransactional = a.annotationType().getAnnotation(Transactional.class);
                    }
                }
            }
        }
        return jtaTransactional;
    }

    private boolean doRollback(Throwable givenException, Transactional transactional) {
        for (Class c: transactional.dontRollbackOn()) {
            if (c.isAssignableFrom(givenException.getClass()))
                return false;
        }
        for (Class c: transactional.rollbackOn()) {
            if (c.isAssignableFrom(givenException.getClass()))
                return true;
        }
        return (RuntimeException.class.isAssignableFrom(givenException.getClass()));
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

}
