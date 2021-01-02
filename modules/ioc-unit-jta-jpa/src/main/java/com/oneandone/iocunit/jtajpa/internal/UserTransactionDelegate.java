package com.oneandone.iocunit.jtajpa.internal;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionalException;
import javax.transaction.UserTransaction;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple;

/**
 * @author aschoerk
 */
public class UserTransactionDelegate implements UserTransaction {

    private final UserTransaction userTransaction;

    private void checkManagedTransaction() throws SystemException {
        if (TransactionImple.getTransaction() != null && TransactionImple.getTransaction().getStatus() != Status.STATUS_NO_TRANSACTION) {
            throw new TransactionalException("UserTransaction not allowed in running simulated container managed transaction", null);
        }
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        checkManagedTransaction();
        userTransaction.begin();
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        // checkManagedTransaction();
        userTransaction.commit();
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        // checkManagedTransaction();
        userTransaction.rollback();
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        // checkManagedTransaction();
        userTransaction.setRollbackOnly();
    }

    @Override
    public int getStatus() throws SystemException {
        return userTransaction.getStatus();
    }

    @Override
    public void setTransactionTimeout(final int seconds) throws SystemException {
        checkManagedTransaction();
        userTransaction.setTransactionTimeout(seconds);
    }

    public UserTransactionDelegate(UserTransaction userTransaction) throws SystemException {
        this.userTransaction = userTransaction;
    }
}
