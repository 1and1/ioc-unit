package com.oneandone.iocunit.ejb.persistence;

import jakarta.ejb.TransactionAttributeType;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;


/**
 * @author aschoerk
 */
public class TestTransaction implements AutoCloseable, UserTransaction {

    /**
     * Start a Transaction according to {@link TransactionAttributeType} as AutoCloseable to make sure at the
     * end of the block the transaction is handled accordingly
     * @param transactionAttributeType  defines the kind of transaction to be started.
     */
    public TestTransaction(TransactionAttributeType transactionAttributeType) {
        new SimulatedTransactionManager().push(transactionAttributeType);
    }

    /**
     * Start a Transaction according to {@link TransactionAttributeType} as AutoCloseable to make sure at the
     * end of the block the transaction is handled accordingly
     * @param persistenceFactory    ignored,
     * @param transactionAttribute  defines the kind of transaction to be started.
     */
    @Deprecated
    public TestTransaction(PersistenceFactory persistenceFactory, TransactionAttributeType transactionAttribute) {
        new SimulatedTransactionManager().push(transactionAttribute);
    }

    /**
     * used according to AutoCloseable to handle the transaction at the end according to {@link TransactionAttributeType}
     * see also {@link AutoCloseable#close}
     * @throws Exception
     *      see {@link AutoCloseable#close}
     */
    @Override
    public void close() throws Exception {
        new SimulatedTransactionManager().pop();
    }

    @Override
    public int getStatus() throws SystemException {
        return new SimulatedTransactionManager().getStatus();
    }

    @Override
    public void rollback() throws SystemException {
        new SimulatedTransactionManager().rollback(null);
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        new SimulatedTransactionManager().setRollbackOnly(null);
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException {
        new SimulatedTransactionManager().commit(null);
    }

    @Override
    public void begin() {
        try {
            new SimulatedTransactionManager().pop();
        } catch (Exception e) {
            new SimulatedTransactionManager().push(TransactionAttributeType.NOT_SUPPORTED);
            throw new RuntimeException(e);
        }
        new SimulatedTransactionManager().push(TransactionAttributeType.REQUIRES_NEW);
    }


    @Override
    public void setTransactionTimeout(int i) throws SystemException {

    }
}
