package com.oneandone.ejbcdiunit.camunda;

import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;

import org.camunda.bpm.engine.impl.cfg.TransactionContext;
import org.camunda.bpm.engine.impl.cfg.TransactionContextFactory;
import org.camunda.bpm.engine.impl.cfg.standalone.StandaloneTransactionContext;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.persistence.SimulatedTransactionManager;
import com.oneandone.ejbcdiunit.persistence.TestTransaction;

/**
 * @author aschoerk
 */
public class EjbCdiUnitTransactionContextFactory implements TransactionContextFactory {
    static Logger logger = LoggerFactory.getLogger("EjbCdiUnitTransactionContextFactory");
    final public boolean started;


    public EjbCdiUnitTransactionContextFactory() {
        super();
        started = true;
    }

    @Override
    public TransactionContext openTransactionContext(CommandContext commandContext) {
        return new EjbCdiUnitTransactionContext(commandContext);
    }

    static class EjbCdiUnitTransactionContext extends StandaloneTransactionContext {

        TestTransaction testTransaction;

        public EjbCdiUnitTransactionContext(CommandContext commandContext) {
            super(commandContext);
            logger.trace("********* Start context for {}", Thread.currentThread().getName());
            new SimulatedTransactionManager().push(TransactionAttributeType.NOT_SUPPORTED);
        }

        @Override
        public void commit() {
            logger.trace("********* Commit context for {}", Thread.currentThread().getName());
            super.commit();
            try {
                new SimulatedTransactionManager().pop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void rollback() {
            logger.trace("******* Rollback context for {}", Thread.currentThread().getName());
            super.rollback();
            try {
                new SimulatedTransactionManager().setRollbackOnly(false);
                new SimulatedTransactionManager().pop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isTransactionActive() {
            try {
                return super.isTransactionActive()
                        && testTransaction.getStatus() != Status.STATUS_NO_TRANSACTION;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
