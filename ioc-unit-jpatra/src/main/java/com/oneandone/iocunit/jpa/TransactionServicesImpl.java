package com.oneandone.iocunit.jpa;

import javax.transaction.Synchronization;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;

import com.oneandone.iocunit.jpa.tra.SimulatedTransactionManager;
import com.oneandone.iocunit.jpa.tra.SimulatedUserTransaction;

/**
 * @author aschoerk
 */
public class TransactionServicesImpl implements TransactionServices{
    @Override
    public void registerSynchronization(Synchronization synchronization) {
        new SimulatedTransactionManager().registerSynchronisation(synchronization);
    }

    @Override
    public boolean isTransactionActive() {
        return false;
    }

    @Override
    public UserTransaction getUserTransaction() {
        return new SimulatedUserTransaction();
    }

    @Override
    public void cleanup() {

    }
}
