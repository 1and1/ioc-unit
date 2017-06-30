package com.oneandone.ejbcdiunit.internal;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ejb.Asynchronous;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.AsynchronousManager;

/**
 * Used to simulate Asynchronous calls.
 *
 * @author aschoerk
 */
@Interceptor
@EjbAsynchronous
public class AsynchronousMethodInterceptor {

    @Inject
    AsynchronousManager asynchronousManager;

    Logger logger = LoggerFactory.getLogger("AsynchronousMethodInterceptor");

    /**
     * Used by Asynchronous Simulation to create a changeable Result-Future
     */
    public final class InterceptedAsyncResult implements Future<Object> {

        Object result = null;
        Throwable exception = null;
        boolean done = false;


        /**
         * set exception to be returned in ExecutionException
         *
         * @return exception to be returned in ExecutionException
         */
        public Throwable getException() {
            return exception;
        }

        /**
         * set exception to be returned in ExecutionException
         * @param exception1 exception to be returned in ExecutionException
         */
        public void setException(Throwable exception1) {
            if (exception1 instanceof  ExecutionException) {
                this.exception = exception1.getCause();
            } else {
                this.exception = exception1;
            }
            this.done = true;
        }

        /**
         * set result to be returned by Future.. isDone will be true.
         * @param result1 the result to be returned .
         */
        public void setResult(final Object result1) {
            this.result = result1;
            this.done = true;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }


        @Override
        public boolean isCancelled() {
            return false;
        }


        @Override
        public boolean isDone() {
            if (!asynchronousManager.doesEnqueAsynchronousCalls()) {
                try {
                    asynchronousManager.oneShotOnly();
                } catch (Throwable thw) {
                    if (thw instanceof ExecutionException) {
                        this.exception = thw.getCause();
                    } else {
                        this.exception = thw;
                    }
                }
            }
            return done || result != null || exception != null;
        }


        @Override
        public Object get() throws InterruptedException, ExecutionException {
            return innerGet();
        }

        private Object innerGet() throws ExecutionException {
            if (result == null) {
                isDone();
            }
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return result;
        }


        @Override
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return innerGet();
        }

        /**
         * allow to create isDone to true.
         */
        public void setDone() {
            this.done = true;
        }
    }

    /**
     * used to prevent infinite recursion.
     */
    private static ThreadLocal<Boolean> inInvoking = new ThreadLocal<Boolean>() {

        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };


    /**
     * in case of an asynchronous method simulate by returning a future which will be filled later by
     * AsynchronousManager-Call.
     * @param ctx the InvocationContext
     * @return the result of the intercepted function
     * @throws Exception if the intercepted function produces an exception.
     */
    @AroundInvoke
    public Object invoking(final InvocationContext ctx) throws Exception {
        if (inInvoking.get()) {
            return ctx.proceed();
        } else {
            logger.info("calling AsynchronousMethodIntercepted Class");
            final Class<?> declaringClass = ctx.getMethod().getDeclaringClass();
            boolean classIsAsynchronous = declaringClass.getAnnotation(Asynchronous.class) != null;
            if (!classIsAsynchronous) {
                if (ctx.getMethod().getAnnotation(Asynchronous.class) == null) {
                    return ctx.proceed();
                }
            }

            final InterceptedAsyncResult asyncResult = new InterceptedAsyncResult();
            final Method m = ctx.getMethod();
            final Object[] p = ctx.getParameters();
            final Object target = ctx.getTarget();

            asynchronousManager.addOneTimeHandler(new Runnable() {
                @Override
                public void run() {
                    try {
                        inInvoking.set(true);
                        Future<?> res = (Future<?>) m.invoke(target, p);
                        asyncResult.setDone();
                        asyncResult.setResult(res.get());
                    } catch (Exception e) {
                        asyncResult.setException(e);
                    } finally {
                        inInvoking.set(false);
                    }
                }
            });
            asyncResult.isDone();  // trigger once
            return asyncResult;
        }
    }

}
