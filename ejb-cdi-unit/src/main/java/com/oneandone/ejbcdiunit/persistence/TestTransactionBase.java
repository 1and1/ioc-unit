package com.oneandone.ejbcdiunit.persistence;

import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;

/**
 * TestTransaction keeps the information about simulated EJB-Transactions for one EntityManager.
 * @author aschoerk
 */
public class TestTransactionBase {
    private final PersistenceFactory persistenceFactory;
    private boolean dropEntityManager = false;
    private boolean closeIt = false;
    private boolean isClosed = false;

    private TestTransactionBase embedding;

    /**
     * Start a Transaction according to {@link TransactionAttributeType} as AutoCloseable to make sure at the
     * end of the block the transaction is handled accordingly
     * @param persistenceFactory    The {@link PersistenceFactory} used to create and register EntityManagers for
     *                              the current thread if necessary.
     * @param transactionAttribute  defines the kind of transaction to be started.
     * @param embedding             the previous TestTransactionBase for this persistenceFactory
     */
    public TestTransactionBase(PersistenceFactory persistenceFactory,
                               TransactionAttributeType transactionAttribute,
                               TestTransactionBase embedding) {

        this.embedding = embedding;
        TransactionAttributeType transactionAttributeType = transactionAttribute;
        this.persistenceFactory = persistenceFactory;
        EntityTransaction tra = embedding == null ? null : persistenceFactory.get().getTransaction();
        switch (transactionAttribute) {
            case SUPPORTS:
                if (embedding == null || !tra.isActive()) {
                    persistenceFactory.createAndRegister();
                    dropEntityManager = true;
                }
                break;
            case NOT_SUPPORTED:
                persistenceFactory.createAndRegister();
                dropEntityManager = true;
                break;
            case NEVER:
                if (tra.isActive()) {
                    throw new TransactionRequiredException("Transaction is not allowed");
                }
                break;
            case MANDATORY:
                if (embedding == null || !tra.isActive()) {
                    throw new TransactionRequiredException("Mandatory Transaction");
                }
                break;
            case REQUIRED:
                if (embedding == null || !tra.isActive()) {
                    tra = persistenceFactory.get().getTransaction();
                    closeIt = true;
                    tra.begin();
                }
                break;
            case REQUIRES_NEW:
                persistenceFactory.createAndRegister();
                persistenceFactory.get().getTransaction().begin();
                closeIt = true;
                dropEntityManager = true;
                break;
            default:
                throw new RuntimeException("Invalid TransactionAttribute " + transactionAttribute);
        }
    }

    /**
     * used according to AutoCloseable to handle the transaction at the end according to {@link TransactionAttributeType}
     * see also {@link AutoCloseable#close}
     * @throws Exception
     *      see {@link AutoCloseable#close}
     */
    public void close() throws Exception {
            close(false);

    }


    /**
     * Close the Transactioncontext
     *
     * @param rollbackOnly rollbackonly was set externally
     * @throws Exception as possible during commit or rollback of transactions.
     */
    public void close(boolean rollbackOnly) throws Exception {
        if (!isClosed) {
            try {
                if (closeIt) {
                    EntityTransaction tra = persistenceFactory.get().getTransaction();
                    if (tra.isActive()) {
                        if (rollbackOnly || tra.getRollbackOnly()) {
                            tra.rollback();
                        } else {
                            tra.commit();
                        }
                    }
                }
            } finally {
                if (dropEntityManager) {
                    persistenceFactory.unRegister();
                    dropEntityManager = false;
                }
                isClosed = true;
            }
        }
    }

    public void setRollbackOnly() {
        EntityTransaction tra = persistenceFactory.get().getTransaction();
        if (tra.isActive()) {
            tra.setRollbackOnly();
        }
    }

    public int getStatus() throws SystemException {
        EntityTransaction transaction = persistenceFactory.get().getTransaction();
        int result = Status.STATUS_NO_TRANSACTION;
        if (transaction.isActive()) {
            if (transaction.getRollbackOnly()) {
                result = Status.STATUS_MARKED_ROLLBACK;
            } else {
                result = Status.STATUS_ACTIVE;
            }
        }
        return result;
    }

    public void setTransactionTimeout(int i) throws SystemException {

    }

    public PersistenceFactory getPersistenceFactory() {
        return persistenceFactory;
    }

    public void rollback() {
        EntityTransaction tra = persistenceFactory.get().getTransaction();
        try {
            if (tra.isActive()) {
                tra.rollback();
            }
        } finally {
            if (!dropEntityManager && embedding != null) {
                embedding.rollback();
            }
        }
    }

    public void begin() {
        EntityTransaction tra = persistenceFactory.get().getTransaction();
        tra.begin();
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        EntityTransaction tra = persistenceFactory.get().getTransaction();
        try {
            if (tra.isActive()) {
                tra.commit();
            }
        } finally {
            if (dropEntityManager) {
                persistenceFactory.unRegister();
                dropEntityManager = false;
            }
        }

    }
}
