/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oneandone.iocunit.jtajpa.narayana.cdi;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionalException;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.jtajpa.internal.UserTransactionDelegate;

/**
 * SPI extension point of the Weld for integrate with transaction manager.
 * If the interface is implemented by the deployment the Weld stops to show
 * info message:
 * <p>
 * <code>
 * WELD-000101: Transactional services not available. Injection of @Inject UserTransaction not available.
 * Transactional observers will be invoked synchronously.
 * </code>
 * </p>
 */
public class CDITransactionServices implements TransactionServices {
    private static final Logger LOG = LoggerFactory.getLogger(CDITransactionServices.class);

    private UserTransaction lastUserTransaction;

    @Override
    public void registerSynchronization(Synchronization synchronizedObserver) {
        try {
            com.arjuna.ats.jta.TransactionManager.transactionManager()
                    .getTransaction().registerSynchronization(synchronizedObserver);
        } catch (SystemException | IllegalStateException | RollbackException e) {
            throw new IllegalStateException("Cannot register synchronization observer " + synchronizedObserver
                                            + " to the available transaction", e);
        }
    }

    @Override
    public boolean isTransactionActive() {
        try {
            int status = com.arjuna.ats.jta.TransactionManager.transactionManager().getStatus();
            switch (status) {
                case Status.STATUS_ACTIVE:
                case Status.STATUS_COMMITTING:
                case Status.STATUS_MARKED_ROLLBACK:
                case Status.STATUS_PREPARED:
                case Status.STATUS_PREPARING:
                case Status.STATUS_ROLLING_BACK:
                    return true;
                default:
                    return false;
            }
        } catch (SystemException se) {
            LOG.error("Cannot obtain the status of the transaction", se);
            return false;
        }
    }

    @Override
    public UserTransaction getUserTransaction() {
        try {
            lastUserTransaction = new UserTransactionDelegate(com.arjuna.ats.jta.UserTransaction.userTransaction());
            return lastUserTransaction;

        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            if(lastUserTransaction != null && lastUserTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                lastUserTransaction.rollback();
            }
            lastUserTransaction = null;
        } catch (TransactionalException | SystemException e) {
            LOG.warn("Cleaning up UserTransaction", e);
        }
    }
}