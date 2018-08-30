package com.oneandone.ejbcdiunit.resourcesimulators;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.oneandone.ejbcdiunit.persistence.SimulatedTransactionManager;

/**
 * A Usertransaction producable by the (ejb-cdi-unit) PersistenceFactory
 *
 * Created by aschoerk on 06.02.14.
 */
public class SimulatedUserTransaction implements UserTransaction {

    public static boolean transactionIsRunning(UserTransaction userTransaction) throws SystemException {
        return (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION && userTransaction.getStatus() != Status.STATUS_ROLLEDBACK
                && userTransaction.getStatus() != Status.STATUS_COMMITTED);
    }


    @Override
    public void begin() throws NotSupportedException, SystemException {
        if (transactionIsRunning(this)) {
            throw new NotSupportedException("UserTransaction already started");
        }
        new SimulatedTransactionManager().push();
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            SecurityException, IllegalStateException, SystemException {
        new SimulatedTransactionManager().commit(true);
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        new SimulatedTransactionManager().rollback(true);
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        new SimulatedTransactionManager().setRollbackOnly(true);
    }

    @Override
    public int getStatus() throws SystemException {
        return new SimulatedTransactionManager().getStatus();
    }

    @Override
    public void setTransactionTimeout(final int i) throws SystemException {
        // ignore here
    }
}
