package com.oneandone.cdi.discoveryrunner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.transaction.Synchronization;
import javax.transaction.UserTransaction;

/**
 * @author aschoerk
 */
public class TransactionServices implements org.jboss.weld.transaction.spi.TransactionServices {
    @Override
    public void registerSynchronization(final Synchronization synchronizedObserver) {

    }

    @Override
    public boolean isTransactionActive() {
        return false;
    }

    @Override
    public UserTransaction getUserTransaction() {
        return (UserTransaction) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{UserTransaction.class}, new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                return null;
            }
        });
    }

    @Override
    public void cleanup() {

    }
}
