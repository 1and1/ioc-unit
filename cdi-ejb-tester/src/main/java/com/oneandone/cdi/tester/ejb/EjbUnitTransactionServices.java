package com.oneandone.cdi.tester.ejb;

import javax.transaction.Synchronization;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;

import com.oneandone.cdi.tester.ejb.persistence.SimulatedTransactionManager;
import com.oneandone.cdi.tester.ejb.resourcesimulators.SimulatedUserTransaction;

/**
 * @author aschoerk
 */
public class EjbUnitTransactionServices implements TransactionServices {
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
