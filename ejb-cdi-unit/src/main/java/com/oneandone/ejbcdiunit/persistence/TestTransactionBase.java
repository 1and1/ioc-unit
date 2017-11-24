package com.oneandone.ejbcdiunit.persistence;

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;

import org.slf4j.MDC;

/**
 * TestTransaction keeps the information about simulated EJB-Transactions for one EntityManager.
 * @author aschoerk
 */
public class TestTransactionBase {
    private final PersistenceFactory persistenceFactory;
    private final TransactionAttributeType transactionAttribute;
    private boolean dropEntityManager = false;
    private boolean closeIt = false;
    private boolean isClosed = false;

    static AtomicInteger tracounter = new AtomicInteger(0);

    private Integer lastXid = null;

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

        this.persistenceFactory = persistenceFactory;
        this.transactionAttribute = transactionAttribute;
        this.embedding = embedding;
        EntityTransaction tra = embedding == null ? null : persistenceFactory.getEntityManager().getTransaction();
        if (embedding != null) {
            if (embedding.transactionAttribute == NOT_SUPPORTED) {
                embedding.getPersistenceFactory().getEntityManager().clear();
            }
        }
        switch (transactionAttribute) {
            case SUPPORTS:
                if (embedding == null) {
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
                } else {
                    persistenceFactory.createAndRegister();
                    dropEntityManager = true;
                }
                break;
            case MANDATORY:
                if (embedding == null || !tra.isActive()) {
                    throw new TransactionRequiredException("Mandatory Transaction");
                }
                break;
            case REQUIRED:
                if (embedding == null || !embedding.traActive()) {
                    persistenceFactory.createAndRegister();
                    tra = persistenceFactory.getEntityManager().getTransaction();
                    dropEntityManager = true;
                    closeIt = true;
                    tra.begin();
                    newXid();
                }
                break;
            case REQUIRES_NEW:
                persistenceFactory.createAndRegister();
                persistenceFactory.getEntityManager().getTransaction().begin();
                newXid();
                closeIt = true;
                dropEntityManager = true;
                break;
            default:
                throw new RuntimeException("Invalid TransactionAttribute " + transactionAttribute);
        }
    }

    private boolean traActive() {
        switch (transactionAttribute) {
            case NOT_SUPPORTED: return false;
            case REQUIRED:
            case REQUIRES_NEW: return true;
            case SUPPORTS: {
                if (embedding == null)
                    return false;
                else
                    return embedding.traActive();
            }
            case NEVER: return false;
            case MANDATORY: return true;
            default:
                throw new RuntimeException("unexpected transaction attribute: " + transactionAttribute);
        }
    }

    private void newXid() {
        assert lastXid == null;
        if (MDC.get("XID") != null)
            lastXid = Integer.parseInt(MDC.get("XID"));
        MDC.put("XID", Integer.toString(tracounter.incrementAndGet()));
    }

    private void resetXid() {
        if (lastXid != null)
            MDC.put("XID", Integer.toString(lastXid));
        else
            MDC.remove("XID");
        lastXid = null;
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
                    EntityTransaction tra = persistenceFactory.getEntityManager().getTransaction();
                    if (tra.isActive()) {
                        if (rollbackOnly || tra.getRollbackOnly()) {
                            tra.rollback();
                        } else {
                            tra.commit();
                        }
                    }
                    resetXid();
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

    public PersistenceFactory getPersistenceFactory() {
        return persistenceFactory;
    }

    void rollback() {
        EntityTransaction tra = persistenceFactory.getEntityManager().getTransaction();
        try {
            if (tra.isActive()) {
                tra.rollback();
                resetXid();
            }
        } finally {
            if (!dropEntityManager && embedding != null) {
                embedding.rollback();
            }
        }
    }

}
