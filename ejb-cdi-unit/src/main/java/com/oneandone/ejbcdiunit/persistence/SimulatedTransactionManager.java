package com.oneandone.ejbcdiunit.persistence;

import static javax.ejb.TransactionAttributeType.MANDATORY;
import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionRolledbackLocalException;
import javax.persistence.TransactionRequiredException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;

/**
 * Instance used to handle threadlocal transaction-stack. Transaction-Interceptors can push the current
 * AttributeType, EntityManagers can find out as soon as they are used, what kind of transactions they need
 * nest in.
 *
 * @author aschoerk
 */
public class SimulatedTransactionManager {

    private static AtomicBoolean activeTransactionInterceptor = new AtomicBoolean();

    private static ThreadLocal<ArrayList<ThreadLocalTransactionInformation>> transactionStack = new ThreadLocal<>();

    private static ThreadLocal<ArrayList<Synchronization>> synchronizations = new ThreadLocal<>();

    private static ConcurrentLinkedQueue<ArrayList<ThreadLocalTransactionInformation>> allStacks
            = new ConcurrentLinkedQueue<>();

    private static TestTransactionBase getTestTransactionBase(int i, PersistenceFactory persistenceFactory) {
        if (i < 0) {
            return null;
        }
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        for (TestTransactionBase testTransactionBase : stack.get(i).persistenceFactories) {
            if (testTransactionBase.getPersistenceFactory().equals(persistenceFactory)) {
                return testTransactionBase;
            }
        }
        return null;
    }

    public void registerSynchronisation(Synchronization synchronization) {
        ArrayList<Synchronization> synchs = initSynchronizations();
        synchs.add(synchronization);
    }

    public void synchBeforeCompletion() {
        if (synchronizations.get() != null) {
            for (Synchronization s : synchronizations.get()) {
                s.beforeCompletion();
            }
        }
    }

    public void synchAfterCompletion(int res) {
        if (synchronizations.get() != null) {
            for (Synchronization s : synchronizations.get()) {
                s.afterCompletion(res);
            }
        }
    }

    /**
     * called by EjbExtensionExtended to clear static data.
     */
    public void init() {
        try {
            for (ArrayList<ThreadLocalTransactionInformation> stack : allStacks) {
                if (stack != null) {
                    try {
                        for (int i = stack.size() - 1; i >= 0; i--) {
                            for (TestTransactionBase ttb : stack.get(i).persistenceFactories) {
                                try {
                                    ttb.close(true);
                                } catch (Throwable e) {

                                }
                            }
                        }
                    } finally {
                        stack.clear();
                    }
                }
            }
            PersistenceFactory.clearPersistenceUnitNames();
        } finally {
            activateTransactionInterceptor();
        }
    }

    public void deactivateTransactionInterceptor() {
        activeTransactionInterceptor.set(false);
    }

    public void activateTransactionInterceptor() {
        activeTransactionInterceptor.set(true);
    }

    public boolean hasActiveTransactionInterceptor() {
        return activeTransactionInterceptor.get();
    }

    /**
     * Start next transactioncontext of this thread by pushing the {@link TransactionAttributeType} on the stack.
     *
     * @param transactionAttributeType The Transactionattribute to be stacked.
     */
    public void push(TransactionAttributeType transactionAttributeType) {
        ArrayList<ThreadLocalTransactionInformation> stack = initStack();
        stack.add(new ThreadLocalTransactionInformation(transactionAttributeType));
    }

    private ArrayList<Synchronization> initSynchronizations() {
        ArrayList<Synchronization> synchs = synchronizations.get();
        if (synchs == null) {
            synchs = new ArrayList<>();
            synchronizations.set(synchs);
        }
        return synchs;
    }

    private ArrayList<ThreadLocalTransactionInformation> initStack() {
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        if (stack == null) {
            stack = new ArrayList<>();
            transactionStack.set(stack);
            allStacks.add(stack);
        }
        return stack;
    }

    /**
     * Start next transactioncontext of this thread by pushing the {@link TransactionAttributeType} on the stack.
     *
     */
    public void push() {
        ArrayList<ThreadLocalTransactionInformation> stack = initStack();
        stack.add(new ThreadLocalTransactionInformation());
    }

    private int findTestTransactionBase(PersistenceFactory persistenceFactory) {
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (getTestTransactionBase(i, persistenceFactory) != null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Let the PersistenceContext take part in the current transaction. If there are further nested contextes to handle,
     * but have not been started yet because it was not needed yet, than start them in correct order.
     *
     * @param persistenceFactory The persistenceFactory managing a specific Persistencecontext of the current thread.
     */
    public void takePart(PersistenceFactory persistenceFactory) {
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        if (stack == null || stack.isEmpty()) {
            push(TransactionAttributeType.NOT_SUPPORTED);
            transactionStack.get().get(0).setTestTransaction();
            stack = transactionStack.get();
        }
        if (stack.get(stack.size() - 1).isRolledBack()) {
            throw new TransactionRolledbackLocalException("Simulated Transaction Manager");
        }
        int pos = findTestTransactionBase(persistenceFactory);
        if (pos == stack.size() - 1) {
            return;
        }
        TestTransactionBase act = getTestTransactionBase(pos, persistenceFactory);
        for (int i = pos + 1; i < stack.size(); i++) {
            final ThreadLocalTransactionInformation threadLocalTransactionInformation = stack.get(i);
            TransactionAttributeType attribute = threadLocalTransactionInformation.transactionAttributeType;
            final TestTransactionBase newTestTransactionBase = new TestTransactionBase(persistenceFactory, attribute, act);
            threadLocalTransactionInformation.persistenceFactories.add(newTestTransactionBase);
            act = newTestTransactionBase;
        }
    }

    /**
     * Make sure the current transaction is rolled back for all persistencefactories which take part.
     *
     * @param expectUserTransaction if not null, rollback is initiated during UserTransaction,
     *                              this is only allowed in UserTransaction, if false, container managed transaction will
     *                              be popped later.
     * @throws IllegalStateException used to simulated UserTransactionInterface
     * @throws SecurityException used to simulated UserTransactionInterface
     * @throws SystemException used to simulated UserTransactionInterface
     */
    public void rollback(Boolean expectUserTransaction) throws IllegalStateException, SecurityException, SystemException {
        synchBeforeCompletion();
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        handleUserTransactionOrNot(expectUserTransaction, stack);
        ThreadLocalTransactionInformation element = stack.get(stack.size() - 1);
        if (expectUserTransaction == null || expectUserTransaction) {
            stack.remove(stack.size() - 1);
        }
        ArrayList<Exception> exceptions = new ArrayList<>();
        for (TestTransactionBase testTransactionBase: element.persistenceFactories) {
            try {
                element.setRolledBack();
                testTransactionBase.rollback();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }

        try {
            handleExceptions(exceptions);
        } catch (RollbackException e) {
            throw new RuntimeException(e);
        }
        synchAfterCompletion(Status.STATUS_ROLLEDBACK);

    }

    /**
     * Make sure the current transaction is committed for all persistencefactories which take part.
     * If setRollbackOnly than do rollback instead.
     * No distributed Transactionmanagement.
     *
     * @param expectUserTransaction if not null, commit is initiated during UserTransaction, this is only allowed in UserTransaction
     * @throws RollbackException used to simulated UserTransactionInterface
     * @throws HeuristicMixedException  used to simulated UserTransactionInterface
     * @throws HeuristicRollbackException used to simulated UserTransactionInterface
     * @throws SecurityException used to simulated UserTransactionInterface
     * @throws IllegalStateException used to simulated UserTransactionInterface
     * @throws SystemException if an exception occurs during the commit or rollback.
     */
    public void commit(Boolean expectUserTransaction) throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            SecurityException, IllegalStateException, SystemException {
        synchBeforeCompletion();
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        handleUserTransactionOrNot(expectUserTransaction, stack);
        if (stack.get(stack.size() - 1).isRolledBack()) {
            rollback(true);
            throw new RollbackException("Can't commit because rolled back");
        }
        ThreadLocalTransactionInformation element = stack.get(stack.size() - 1);
        boolean setRollbackOnly = element.getRollbackOnly();
        ArrayList<Exception> exceptions = new ArrayList<>();
        for (TestTransactionBase testTransactionBase: element.persistenceFactories) {
            try {
                testTransactionBase.close(setRollbackOnly);
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        if (element.isUserTransaction()) {
            stack.remove(stack.size() - 1);
        }
        if (setRollbackOnly && element.isUserTransaction()) {
            throw new RollbackException("Test Transaction fails.");
        }
        handleExceptions(exceptions);
        synchAfterCompletion(Status.STATUS_COMMITTED);
    }

    private void handleUserTransactionOrNot(Boolean expectUserTransaction,
                                            ArrayList<ThreadLocalTransactionInformation> stack) throws IllegalStateException {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalStateException("No Transaction Context");
        }
        ThreadLocalTransactionInformation element = stack.get(stack.size() - 1);
        if (expectUserTransaction != null && expectUserTransaction  != element.isUserTransaction()) {
            throw new IllegalStateException("Trying to setRollbackOnly in wrong transaction state");
        }

    }

    /**
     * make sure the current Transaction will be rolledback as soon as the commit is tried
     *
     * @param expectUserTransaction if not null, it is expected that the current Transaction is a Bean Managed Transaction
     * @throws IllegalStateException in case of wrong Transactiontype (container or bean managed)
     */
    public void setRollbackOnly(Boolean expectUserTransaction) throws IllegalStateException {
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        handleUserTransactionOrNot(expectUserTransaction, stack);

        ThreadLocalTransactionInformation element = stack.get(stack.size() - 1);
        element.setRollbackOnly();
    }

    /**
     * check if it is made sure the current Transaction will be rolledback as soon as the commit is tried
     *
     * @param expectUserTransaction if not null, it is expected that the current Transaction is a Bean Managed Transaction
     * @return true if rollbackonly is set
     * @throws IllegalStateException in case of wrong Transactiontype (container or bean managed)
     */
    public boolean getRollbackOnly(Boolean expectUserTransaction) throws IllegalStateException {
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        handleUserTransactionOrNot(expectUserTransaction, stack);
        ThreadLocalTransactionInformation element = stack.get(stack.size() - 1);
        return element.getRollbackOnly();
    }

    private void handleExceptions(ArrayList<Exception> exceptions) throws RollbackException {
        if (exceptions.size() == 1) {
            final Exception currentException = exceptions.get(0);
            if (currentException instanceof javax.transaction.RollbackException) {
                throw (RollbackException) currentException;
            } else if (currentException instanceof javax.persistence.TransactionRequiredException) {
                throw (TransactionRequiredException) currentException;
            } else {
                throw new RuntimeException(currentException);
            }
        } else if (exceptions.size() > 1) {
            throw new RuntimeException("Combined Exceptions in multiple Transactions");
        }
    }

    /**
     * End of current transactioncontext for all persistencecontexts which take part.
     * This might mean, that no action is done, e.g. in case of nested Required.
     * This is correctly handled by the TestTransaction#close.
     *
     * @throws Exception if an exception occurs during the transaction handling
     */
    public void pop() throws Exception {
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        if (stack.size() > 0) {
            ThreadLocalTransactionInformation element = stack.remove(stack.size() - 1);
            ArrayList<Exception> exceptions = new ArrayList<>();
            for (TestTransactionBase testTransactionBase : element.persistenceFactories) {
                try {
                    testTransactionBase.close(element.getRollbackOnly() || element.isRolledBack());
                } catch (Exception e) {
                    exceptions.add(e);
                }
            }
            handleExceptions(exceptions);
        }
    }

    /**
     * simulate getStatus from UserTransaction
     * @return the Status according to UserTransaction
     * @throws SystemException as simulated
     */
    public int getStatus() throws SystemException {
        ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
        if (stack == null || stack.isEmpty()) {
            return Status.STATUS_NO_TRANSACTION;
        }
        ThreadLocalTransactionInformation element = stack.get(stack.size() - 1);

        return element.getStatus();
    }

    /**
     * used as thread local information stacked to reflect ejb-transaction-contextes. Persistence-Factories taking part a added on demand.
     */
    static class ThreadLocalTransactionInformation {
        ThreadLocalTransactionInformation previous = calcPrevious();
        TransactionAttributeType transactionAttributeType;
        boolean rollbackOnly = false;
        boolean userTransaction = false;
        boolean testTransaction = false;
        List<TestTransactionBase> persistenceFactories = new ArrayList<>();
        private boolean rolledBack;

        ThreadLocalTransactionInformation(TransactionAttributeType transactionAttributeType) {
            this.transactionAttributeType = transactionAttributeType;
            userTransaction = false;
        }

        ThreadLocalTransactionInformation() {
            this.transactionAttributeType = REQUIRES_NEW;
            userTransaction = true;
        }

        public boolean isTestTransaction() {
            return testTransaction;
        }

        public void setTestTransaction() {
            this.testTransaction = true;
        }

        public ThreadLocalTransactionInformation getPrevious() {
            return previous;
        }

        private ThreadLocalTransactionInformation calcPrevious() {
            ArrayList<ThreadLocalTransactionInformation> stack = transactionStack.get();
            if (!stack.isEmpty()) {
                return stack.get(stack.size() - 1);
            } else {
                return null;
            }
        }

        void setRollbackOnly() {
            rollbackOnly = true;
            if (transactionAttributeType == REQUIRED && previous != null) {
                if (previous.transactionAttributeType == REQUIRED || previous.transactionAttributeType == REQUIRES_NEW) {
                    previous.setRollbackOnly();
                }
            }
        }

        boolean getRollbackOnly() {
            return rollbackOnly;
        }

        boolean isUserTransaction() {
            return userTransaction;
        }

        public void setRolledBack() {
            rolledBack = true;
            if (previous != null) {
                if (transactionAttributeType == REQUIRED
                        || transactionAttributeType == SUPPORTS
                        || transactionAttributeType == MANDATORY) {
                    if (previous.transactionAttributeType != NOT_SUPPORTED) {
                        previous.setRolledBack();
                    }
                }
            }
        }

        public boolean isRolledBack() {
            return rolledBack;
        }

        public int getStatus() {
            if (isUserTransaction() || transactionAttributeType == REQUIRED
                    || transactionAttributeType == REQUIRES_NEW
                    || transactionAttributeType == MANDATORY) {
                if (getRollbackOnly()) {
                    return Status.STATUS_MARKED_ROLLBACK;
                } else if (isRolledBack()) {
                    return Status.STATUS_NO_TRANSACTION;
                } else {
                    return Status.STATUS_ACTIVE;
                }
            } else if (transactionAttributeType == SUPPORTS) {
                return getPrevious() != null ? getPrevious().getStatus() : Status.STATUS_NO_TRANSACTION;
            } else {
                return Status.STATUS_NO_TRANSACTION;
            }
        }
    }

}
