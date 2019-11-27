package com.oneandone.iocunit.ejb.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class IocUnitTransactionSynchronizationRegistry implements TransactionSynchronizationRegistry {
    @Override
    public Object getTransactionKey() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void putResource(final Object key, final Object value) {
        // ignore
    }

    @Override
    public Object getResource(final Object key) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void registerInterposedSynchronization(final Synchronization sync) {
        new SimulatedTransactionManager().registerSynchronisation(sync);
    }

    @Override
    public int getTransactionStatus() {
        try {
            return new SimulatedTransactionManager().getStatus();
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRollbackOnly() {
        new SimulatedTransactionManager().setRollbackOnly(null);
    }

    @Override
    public boolean getRollbackOnly() {
        return new SimulatedTransactionManager().getRollbackOnly(null);
    }
}
