package com.oneandone.ejbcdiunit.persistence;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;

/**
 * Simulates the EntityTransaction as it is returned by the EntityManagerDelegates
 *
 * @author aschoerk
 */
class SimulatedEntityTransaction implements EntityTransaction {
    /**
     * Start a resource transaction.
     *
     * @throws IllegalStateException if <code>isActive()</code> is true
     */
    @Override
    public void begin() {
        new SimulatedTransactionManager().push();
    }

    /**
     * Commit the current resource transaction, writing any
     * unflushed changes to the database.
     *
     * @throws IllegalStateException if <code>isActive()</code> is false
     * @throws RollbackException     if the commit fails
     */
    @Override
    public void commit() throws IllegalStateException, RollbackException {
        try {
            new SimulatedTransactionManager().commit(true);
        } catch (javax.transaction.RollbackException e) {
            throw new RollbackException(e);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Roll back the current resource transaction.
     *
     * @throws IllegalStateException if <code>isActive()</code> is false
     * @throws PersistenceException  if an unexpected error
     *                               condition is encountered
     */
    @Override
    public void rollback() throws IllegalStateException, PersistenceException {
        try {
            new SimulatedTransactionManager().rollback(true);
        }  catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Mark the current resource transaction so that the only
     * possible outcome of the transaction is for the transaction
     * to be rolled back.
     *
     * @throws IllegalStateException if <code>isActive()</code> is false
     */
    @Override
    public void setRollbackOnly() throws IllegalStateException {
        try {
            new SimulatedTransactionManager().setRollbackOnly(true);
        }  catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Determine whether the current resource transaction has been
     * marked for rollback.
     *
     * @return boolean indicating whether the transaction has been
     * marked for rollback
     * @throws IllegalStateException if <code>isActive()</code> is false
     */
    @Override
    public boolean getRollbackOnly() {
        try {
            return new SimulatedTransactionManager().getRollbackOnly(true);
        }  catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Indicate whether a resource transaction is in progress.
     *
     * @return boolean indicating whether transaction is
     * in progress
     * @throws PersistenceException if an unexpected error
     *                              condition is encountered
     */
    @Override
    public boolean isActive() {
        try {
            final int status = new SimulatedTransactionManager().getStatus();
            return  status == Status.STATUS_ACTIVE
                    || status == Status.STATUS_MARKED_ROLLBACK;
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }
}
