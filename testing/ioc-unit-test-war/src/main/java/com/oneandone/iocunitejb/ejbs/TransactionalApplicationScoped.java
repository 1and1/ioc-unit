package com.oneandone.iocunitejb.ejbs;

import static javax.transaction.Transactional.TxType.NOT_SUPPORTED;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;

import com.oneandone.iocunitejb.entities.TestEntity1;


/**
 * @author aschoerk
 */
@ApplicationScoped
public class TransactionalApplicationScoped {

    @Inject
    Logger logger;

    @Inject
    private Instance<TransactionalApplicationScoped> self;

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

    @Transactional(REQUIRED)
    public TestEntity1 saveInCurrentTransaction(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @Transactional(REQUIRES_NEW)
    public TestEntity1 saveInNewTransaction(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @Transactional(SUPPORTS)
    public TestEntity1 saveInSupportedTransaction(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @Transactional(NOT_SUPPORTED)
    public TestEntity1 trySaveInNotSupportedTransaction(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @Transactional(REQUIRED)
    public TestEntity1 saveInCurrentTransactionAndSaveNewInNewButDirectCall(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        // local call so not really saved in new Transaction!!
        saveInNewTransaction(new TestEntity1());
        return testEntity1;
    }

    @Transactional(REQUIRED)
    public TestEntity1 saveRequiresNewLocalAsBusinessObject(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        // no local call anymore so really saved in new Transaction!!
        self.get().saveInNewTransaction(new TestEntity1());
        return testEntity1;
    }


    @Transactional(REQUIRED)
    public void persistRequiredAndRTException(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        throw new RuntimeException("rt exception after persist in required 1");
    }

    @Transactional(REQUIRED)
    public void persistRequiredAndIOException(TestEntity1 testEntity1) throws IOException {
        entityManager.persist(testEntity1);
        throw new IOException("io exception after persist in required 1");
    }

    @Transactional(REQUIRES_NEW)
    public void persistRequiresNewAndRTException(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        throw new RuntimeException("rt exception after persist in required 2");
    }

    @Transactional(REQUIRES_NEW)
    public void persistRequiresNewAndIOException(TestEntity1 testEntity1) throws IOException {
        entityManager.persist(testEntity1);
        throw new IOException("io exception after persist in required 2");
    }

    @Transactional(REQUIRED)
    public void persistRequiredIndirectAndRTException(TestEntity1 testEntity1) {
        self.get().saveInCurrentTransaction(testEntity1);
        throw new RuntimeException("rt exception after persist in required 3");
    }

    @Transactional(REQUIRED)
    public void persistRequiredIndirectAndRTExceptionIndirect(TestEntity1 testEntity1) {
        self.get().persistRequiredAndRTException(testEntity1);
        throw new RuntimeException("rt exception after persist in required 4");
    }

    @Transactional(REQUIRED)
    public void persistRequiredIndirectAndIOExceptionIndirect(TestEntity1 testEntity1) throws IOException {
        persistRequiredAndIOException(testEntity1);
        throw new RuntimeException("rt exception after persist in required 5");
    }

    @Transactional(REQUIRED)
    public void persistRequiresNewIndirectAndRTExceptionIndirect(TestEntity1 testEntity1) {
        self.get().persistRequiresNewAndRTException(testEntity1);
        throw new RuntimeException("rt exception after persist in required 6");
    }

    @Transactional(REQUIRED)
    public void persistRequiresNewIndirectAndIOExceptionIndirect(TestEntity1 testEntity1) throws IOException {
        persistRequiresNewAndIOException(testEntity1);
        throw new RuntimeException("rt exception after persist in required 7");
    }

    /*
    @Transactional(REQUIRES_NEW)
    public boolean persistRequiresNewSetRollbackOnlyBySessionContext(TestEntity1 testEntity1) throws IOException {
        entityManager.persist(testEntity1);
        sessionContext.setRollbackOnly();
        return sessionContext.getRollbackOnly();
    }

    @Transactional(REQUIRES_NEW)
    public boolean persistRequiresNewGetRollbackOnlyBySessionContext(TestEntity1 testEntity1) throws IOException {
        entityManager.persist(testEntity1);
        return sessionContext.getRollbackOnly();
    }
    */


}
