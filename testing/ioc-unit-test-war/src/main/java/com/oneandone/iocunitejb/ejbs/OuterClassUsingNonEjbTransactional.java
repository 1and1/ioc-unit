package com.oneandone.iocunitejb.ejbs;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
public class OuterClassUsingNonEjbTransactional {

    @Inject
    TransactionalApplicationScoped statelessCdiBean;

    @Transactional(REQUIRED)
    public void saveNewInRequired(TestEntity1 testEntity1) {
        statelessCdiBean.saveInNewTransaction(testEntity1);
    }

    @Transactional(REQUIRED)
    public void saveNewInRequiredThrowRTException(TestEntity1 testEntity1) {
        statelessCdiBean.saveInNewTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @Transactional(REQUIRED)
    public void saveRequiredInRequired(TestEntity1 testEntity1) {
        statelessCdiBean.saveInCurrentTransaction(testEntity1);
    }

    @Transactional(REQUIRED)
    public void saveRequiredInRequiredThrowException(TestEntity1 testEntity1) {
        statelessCdiBean.saveInCurrentTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @Transactional(REQUIRES_NEW)
    public void saveNewInNewTra(TestEntity1 testEntity1) {
        statelessCdiBean.saveInNewTransaction(testEntity1);

    }

    @Transactional(REQUIRES_NEW)
    public void saveRequiredInNewTraThrow(TestEntity1 testEntity1) {
        statelessCdiBean.saveInCurrentTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @Transactional(REQUIRES_NEW)
    public void saveNewInNewTraThrow(TestEntity1 testEntity1) {
        statelessCdiBean.saveInNewTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @Transactional(REQUIRES_NEW)
    public void saveRequiredInNewTra(TestEntity1 testEntity1) {
        statelessCdiBean.saveInCurrentTransaction(testEntity1);
    }

    @Transactional(REQUIRES_NEW)
    public void saveRequiredPlusNewInNewTra(TestEntity1 testEntity1) {
        statelessCdiBean.saveInCurrentTransactionAndSaveNewInNewButDirectCall(testEntity1);
    }

    @Transactional(REQUIRES_NEW)
    public void saveRequiredPlusNewInNewTraButDirectCallAndThrow(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        statelessCdiBean.saveInCurrentTransactionAndSaveNewInNewButDirectCall(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }


    @Transactional(REQUIRES_NEW)
    public void saveRequiresNewLocalAsBusinessObject(TestEntity1 testEntity1) {
        statelessCdiBean.saveRequiresNewLocalAsBusinessObject(testEntity1);
    }

    @Transactional(REQUIRES_NEW)
    public void saveRequiresNewLocalAsBusinessObjectAndThrow(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        statelessCdiBean.saveRequiresNewLocalAsBusinessObject(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @Transactional(REQUIRES_NEW)
    public void saveToSetRollbackOnlyAndTryAdditionalSave(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        try {
            statelessCdiBean.persistRequiredAndRTException(testEntity1);
        } catch (RuntimeException ex) {
            ;
        }
        statelessCdiBean.saveInCurrentTransaction(new TestEntity1());
        statelessCdiBean.saveInCurrentTransaction(new TestEntity1());
    }


}
