package com.oneandone.ejbcdiunit.cditransactions;

import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit.entities.TestEntity1;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
@Stateless
public class OuterClass {

    @Inject
    TransactionalAppScoped transactionalCdiBean;


    public void saveNewInRequired(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInNewTransaction(testEntity1);
    }

    public void saveNewInRequiredThrowRTException(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInNewTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    public void saveRequiredInRequired(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInCurrentTransaction(testEntity1);
    }

    public void saveRequiredInRequiredThrowException(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInCurrentTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNewInNewTra(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInNewTransaction(testEntity1);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiredInNewTraThrow(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInCurrentTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNewInNewTraThrow(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInNewTransaction(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiredInNewTra(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInCurrentTransaction(testEntity1);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiredPlusNewInNewTra(TestEntity1 testEntity1) {
        transactionalCdiBean.saveInCurrentTransactionAndSaveNewInNewButDirectCall(testEntity1);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRequiredPlusNewInNewTraButDirectCallAndThrow(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        transactionalCdiBean.saveInCurrentTransactionAndSaveNewInNewButDirectCall(testEntity1);
        throw new RuntimeException("exception to rollback transaction");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveToSetRollbackOnlyAndTryAdditionalSave(TestEntity1 testEntity1) {
        // no transaction-interceptor because of local call so both saves will be rolledback
        try {
            transactionalCdiBean.persistRequiredAndRTException(testEntity1);
        } catch (RuntimeException ex) {
            ;
        }
        transactionalCdiBean.saveInCurrentTransaction(new TestEntity1());
        transactionalCdiBean.saveInCurrentTransaction(new TestEntity1());
    }


}
