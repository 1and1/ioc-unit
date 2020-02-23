package com.oneandone.iocunit.jpa.interceptors;

import java.lang.annotation.Annotation;

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
        if(transactionManager.hasActiveTransactionInterceptor()) {
            final Class<?> declaringClass = ctx.getMethod().getDeclaringClass();
            Class<?> targetClass = getTargetClass(ctx);

            incLevel();
            Transactional.TxType savedLastTransactionAttributeType = lastTransactionAttributeType.get();
            Transactional.TxType toPush = null;

            Transactional jtaTransactional = findTransactionalAnnotation(ctx, targetClass, declaringClass);

            if(jtaTransactional == null) {
                throw new RuntimeException("Did not find Transactional-Annotation to push");
            }
            transactionManager.push(jtaTransactional.value());
            lastTransactionAttributeType.set(toPush);

            boolean passThroughRollbackException = true;
            try {
                logger.debug("Thread {} L{} changing  from {} to {} xid: {} in {}.{}",
                        Thread.currentThread().getId(), getLevel(),
                        savedLastTransactionAttributeType == null ? "undefined" : savedLastTransactionAttributeType,
                        toPush, MDC.get("XID"), declaringClass.getSimpleName(), ctx.getMethod().getName());
                return ctx.proceed();
            } catch (Exception ex) {
                logger.debug("Thread {} L{} Exception {} in {} xid: {} in {}.{}",
                        Thread.currentThread().getId(), getLevel(),
                        ex.getClass().getSimpleName(), toPush, MDC.get("XID"), declaringClass.getSimpleName(),
                        ctx.getMethod().getName());

                boolean doRollback =
                        doRollback(ex, jtaTransactional);

                if(doRollback) {
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
                    if(passThroughRollbackException) {
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
        else {
            return ctx.proceed();
        }
    }

    private Transactional findTransactionalAnnotationAtClass(Class clazz) {
        Transactional jtaTransactional = findAnnotation(clazz, Transactional.class);
        if(jtaTransactional == null) {
            for (Annotation a : clazz.getAnnotations()) {
                if(a.annotationType().getAnnotation(Stereotype.class) != null) {
                    jtaTransactional = a.annotationType().getAnnotation(Transactional.class);
                }
            }
        }
        return jtaTransactional;
    }

    private Transactional findTransactionalAnnotation(InvocationContext ctx, Class targetClass, Class declaringClass) {
        Transactional jtaTransactional = ctx.getMethod().getAnnotation(Transactional.class);
        if(jtaTransactional == null) {
            for (Annotation a : ctx.getMethod().getAnnotations()) {
                if(a.annotationType().getAnnotation(Stereotype.class) != null) {
                    jtaTransactional = a.annotationType().getAnnotation(Transactional.class);
                }
            }
        }
        if(jtaTransactional == null) {
            jtaTransactional = findTransactionalAnnotationAtClass(targetClass);
        }
        if(jtaTransactional == null) {
            jtaTransactional = findTransactionalAnnotationAtClass(declaringClass);
        }
        return jtaTransactional;
    }

    private boolean doRollback(Throwable givenException, Transactional transactional) {
        for (Class c : transactional.dontRollbackOn()) {
            if(c.isAssignableFrom(givenException.getClass())) {
                return false;
            }
        }
        for (Class c : transactional.rollbackOn()) {
            if(c.isAssignableFrom(givenException.getClass())) {
                return true;
            }
        }
        return (RuntimeException.class.isAssignableFrom(givenException.getClass()));
    }


}
