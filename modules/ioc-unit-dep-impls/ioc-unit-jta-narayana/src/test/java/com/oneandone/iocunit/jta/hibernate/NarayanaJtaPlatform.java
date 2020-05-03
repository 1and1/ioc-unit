package com.oneandone.iocunit.jta.hibernate;

import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

/**
 * @author aschoerk
 */
public class NarayanaJtaPlatform extends AbstractJtaPlatform {
    private static final long serialVersionUID = -4914946425971880780L;

    @Override
    protected TransactionManager locateTransactionManager() {
        return com.arjuna.ats.jta.TransactionManager.transactionManager();
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return com.arjuna.ats.jta.UserTransaction.userTransaction();
    }

    @Override
    public TransactionManager retrieveTransactionManager() {
        return super.getTransactionManager();
    }

    @Override
    public UserTransaction retrieveUserTransaction() {
        return super.retrieveUserTransaction();
    }

    @Override
    public Object getTransactionIdentifier(final Transaction transaction) {
        return super.getTransactionIdentifier(transaction);
    }

    @Override
    public boolean canRegisterSynchronization() {
        return super.canRegisterSynchronization();
    }

    @Override
    public void registerSynchronization(final Synchronization synchronization) {
        super.registerSynchronization(synchronization);
    }

    @Override
    public int getCurrentStatus() throws SystemException {
        return 0;
    }
}
