package com.oneandone.ejbcdiunit.ejbs;

import java.io.IOException;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.oneandone.ejbcdiunit.entities.TestEntity1;


/**
 * @author aschoerk
 */
@Stateless(name = "StatelessEJB")
public class StatelessEJB {

    @Inject
    Logger logger;

    @Resource
    SessionContext sessionContext;

    @Inject
    EntityManager entityManager;
    private int publicInteger = 200;

    public void method1() {
        logger.info("StatelessEJB: methodCallUsingSessionContext called");
    }

    public TestEntity1 saveInCurrentTransactionDefaultTraAttribute(TestEntity1 testEntity1) {
        logger.info("output public variable {}", publicInteger);
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestEntity1 saveInCurrentTransaction(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TestEntity1 saveInNewTransaction(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TestEntity1 saveInSupportedTransaction(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public TestEntity1 trySaveInNotSupportedTransaction(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestEntity1 saveInCurrentTransactionAndSaveNewInNewButDirectCall(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        // local call so not really saved in new Transaction!!
        saveInNewTransaction(new TestEntity1());
        return testEntity1;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestEntity1 saveRequiresNewLocalAsBusinessObject(TestEntity1 testEntity1) {
        StatelessEJB res = sessionContext.getBusinessObject(StatelessEJB.class);
        entityManager.persist(testEntity1);
        // no local call anymore so really saved in new Transaction!!
        res.saveInNewTransaction(new TestEntity1());
        return testEntity1;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistRequiredAndRTException(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        throw new RuntimeException("rt exception after persist in required 1");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistRequiredAndIOException(TestEntity1 testEntity1) throws IOException {
        entityManager.persist(testEntity1);
        throw new IOException("io exception after persist in required 1");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void persistRequiresNewAndRTException(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        throw new RuntimeException("rt exception after persist in required 2");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void persistRequiresNewAndIOException(TestEntity1 testEntity1) throws IOException {
        entityManager.persist(testEntity1);
        throw new IOException("io exception after persist in required 2");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistRequiredIndirectAndRTException(TestEntity1 testEntity1) {
        StatelessEJB res = sessionContext.getBusinessObject(StatelessEJB.class);
        res.saveInCurrentTransaction(testEntity1);
        throw new RuntimeException("rt exception after persist in required 3");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistRequiredIndirectAndRTExceptionIndirect(TestEntity1 testEntity1) {
        StatelessEJB res = sessionContext.getBusinessObject(StatelessEJB.class);
        res.persistRequiredAndRTException(testEntity1);
        throw new RuntimeException("rt exception after persist in required 4");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistRequiredIndirectAndIOExceptionIndirect(TestEntity1 testEntity1) throws IOException {
        StatelessEJB res = sessionContext.getBusinessObject(StatelessEJB.class);
        res.persistRequiredAndIOException(testEntity1);
        throw new RuntimeException("rt exception after persist in required 5");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistRequiresNewIndirectAndRTExceptionIndirect(TestEntity1 testEntity1) {
        StatelessEJB res = sessionContext.getBusinessObject(StatelessEJB.class);
        res.persistRequiresNewAndRTException(testEntity1);
        throw new RuntimeException("rt exception after persist in required 6");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistRequiresNewIndirectAndIOExceptionIndirect(TestEntity1 testEntity1) throws IOException {
        StatelessEJB res = sessionContext.getBusinessObject(StatelessEJB.class);
        res.persistRequiresNewAndIOException(testEntity1);
        throw new RuntimeException("rt exception after persist in required 7");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean persistRequiresNewSetRollbackOnlyBySessionContext(TestEntity1 testEntity1) throws IOException {
        entityManager.persist(testEntity1);
        sessionContext.setRollbackOnly();
        return sessionContext.getRollbackOnly();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean persistRequiresNewGetRollbackOnlyBySessionContext(TestEntity1 testEntity1) throws IOException {
        entityManager.persist(testEntity1);
        return sessionContext.getRollbackOnly();
    }



}
