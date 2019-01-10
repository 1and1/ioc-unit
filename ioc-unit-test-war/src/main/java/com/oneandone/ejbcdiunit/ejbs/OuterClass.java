package com.oneandone.ejbcdiunit.ejbs;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.oneandone.ejbcdiunit.entities.TestEntity1;

/**
 * @author aschoerk
 */
@Stateless
public class OuterClass {

    @EJB
    StatelessEJB statelessEJB;

    @EJB
    SingletonEJB singletonEJB;

    public void saveNewInRequired(TestEntity1 testEntity1) {
        statelessEJB.saveInNewTransaction(testEntity1);
    }

    public void saveNewInRequiredThrowRTException(TestEntity1 testEntity1) {
        statelessEJB.saveInNewTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    public void saveRequiredInRequired(TestEntity1 testEntity1) {
        statelessEJB.saveInCurrentTransaction(testEntity1);
    }

    public void saveRequiredInRequiredThrowException(TestEntity1 testEntity1) {
        statelessEJB.saveInCurrentTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNewInNewTra(TestEntity1 testEntity1) {
        statelessEJB.saveInNewTransaction(testEntity1);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiredInNewTraThrow(TestEntity1 testEntity1) {
        statelessEJB.saveInCurrentTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNewInNewTraThrow(TestEntity1 testEntity1) {
        statelessEJB.saveInNewTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiredInNewTra(TestEntity1 testEntity1) {
        statelessEJB.saveInCurrentTransaction(testEntity1);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiredPlusNewInNewTra(TestEntity1 testEntity1) {
        statelessEJB.saveInCurrentTransactionAndSaveNewInNewButDirectCall(testEntity1);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiredPlusNewInNewTraButDirectCallAndThrow(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        statelessEJB.saveInCurrentTransactionAndSaveNewInNewButDirectCall(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiresNewLocalAsBusinessObject(TestEntity1 testEntity1) {
        statelessEJB.saveRequiresNewLocalAsBusinessObject(testEntity1);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiresNewLocalAsBusinessObjectAndThrow(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        statelessEJB.saveRequiresNewLocalAsBusinessObject(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiresNewLocalUsingSelf(TestEntity1 testEntity1) {
        singletonEJB.saveRequiresNewLocalUsingSelf(testEntity1);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiresNewLocalUsingSelfAndThrow(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        singletonEJB.saveRequiresNewLocalUsingSelf(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveToSetRollbackOnlyAndTryAdditionalSave(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        try {
            statelessEJB.persistRequiredAndRTException(testEntity1);
        } catch (RuntimeException ex) {
            ;
        }
        statelessEJB.saveInCurrentTransaction(new TestEntity1());
        statelessEJB.saveInCurrentTransaction(new TestEntity1());
    }


}
