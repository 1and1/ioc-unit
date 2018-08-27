package com.oneandone.ejbcdiunit5.ejb;

import com.oneandone.ejbcdiunit.SessionContextFactory;
import com.oneandone.ejbcdiunit.cdiunit.EjbJarClasspath;
import com.oneandone.ejbcdiunit.ejbs.*;
import com.oneandone.ejbcdiunit.ejbs.appexc.TestBaseClass;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.persistence.SinglePersistenceFactory;
import com.oneandone.ejbcdiunit.persistence.TestTransaction;
import com.oneandone.ejbcdiunit.testbases.EJBTransactionTestBase;
import com.oneandone.ejbcdiunit.testbases.TestEntity1Saver;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;
import org.hamcrest.MatcherAssert;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ejb.EJBException;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.transaction.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ StatelessEJB.class, SingletonEJB.class,
        TestEjb.TestDbPersistenceFactory.class, SessionContextFactory.class,
        StatelessBeanManagedTrasEJB.class, StatelessChildEJB.class,
        QMdbEjb.class, MdbEjbInfoSingleton.class, LoggerGenerator.class })
@EjbJarClasspath(TestBaseClass.class)
public class TestEjb extends EJBTransactionTestBase {

    @Inject
    SinglePersistenceFactory persistenceFactory;

    @AfterAll
    public static void tearDownProfiler() throws InterruptedException {
        initMemory();
    }

    private static void initMemory() {
        final Runtime runtime = Runtime.getRuntime();
        runtime.runFinalization();
        long freemem = runtime.freeMemory();
        do {
            runtime.freeMemory();
            if (freemem == runtime.freeMemory()) {
                break;
            }
            freemem = runtime.freeMemory();
        }
        while (true);
        runtime.runFinalization();
    }

    @BeforeEach
    public void setupProfiler() throws InterruptedException {
        initMemory();
        System.getProperties().setProperty("hibernate.show_sql", "true");
    }

    @Test
    public void everythingNotNull() {
        Assertions.assertNotNull(cdiClass);
        Assertions.assertNotNull(cdiClass.getSingletonEJB());
        Assertions.assertNotNull(cdiClass.getStatelessEJB());
        cdiClass.getStatelessEJB().method1();
        cdiClass.getSingletonEJB().methodCallUsingSessionContext();
        cdiClass.getSingletonEJB().methodCallUsingSelf();
        cdiClass.doSomething();
        Assertions.assertNotNull(statelessEJB);
        Assertions.assertNotNull(singletonEJB);
    }

    @Test
    public void testSelfInjection() {
        cdiClass.getSingletonEJB().methodCallUsingSelf();
    }

    @Test
    public void testSelfInjectionByEjb() {
        cdiClass.getSingletonEJB().methodCallUsingSelfEjb();
    }


    @Test
    public void testSessionContextInjection() {
        cdiClass.getSingletonEJB().methodCallUsingSessionContext();
    }

    @Override
    public void runTestInRolledBackTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {
        try (TestTransaction resource1 = persistenceFactory.transaction(TransactionAttributeType.REQUIRES_NEW)) {
            TestEntity1 testEntity1 = new TestEntity1();
            boolean exceptionHappened = false;
            try {
                saver.save(testEntity1);
            } catch (RuntimeException r) {
                exceptionHappened = true;
                if (resource1.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    resource1.rollback();
                }
                if (resource1.getStatus() == Status.STATUS_NO_TRANSACTION) {
                    resource1.begin();
                }
            }
            MatcherAssert.assertThat(exceptionHappened, is(exceptionExpected));
            entityManager.persist(new TestEntity1());
            entityManager.flush();
            Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
            MatcherAssert.assertThat(res.intValue(), is(num));
            resource1.setRollbackOnly();
        } catch (RollbackException rbe) {
             // ignore, wanted to roll it back!!!
        }
    }

    @Override
    @Test
    public void requiresNewMethodWorks() throws Exception {
        super.requiresNewMethodWorks();
    }

    @Override
    @Test
    public void defaultMethodWorks() throws Exception {
        super.defaultMethodWorks();
    }

    @Override
    @Test
    public void requiredMethodWorks() throws Exception {
        super.requiredMethodWorks();
    }

    @Override
    @Test
    public void indirectSaveNewInRequired() throws Exception {
        super.indirectSaveNewInRequired();
    }

    @Override
    @Test
    public void indirectSaveNewInRequiredThrowException() throws Exception {
        super.indirectSaveNewInRequiredThrowException();
    }

    @Override
    @Test
    public void indirectSaveRequiredInRequired() throws Exception {
        super.indirectSaveRequiredInRequired();
    }

    @Override
    @Test
    public void indirectSaveRequiredInRequiredThrowException() throws Exception {
        super.indirectSaveRequiredInRequiredThrowException();
    }

    @Override
    @Test
    public void indirectSaveNewInNewTra() throws Exception {
        super.indirectSaveNewInNewTra();
    }

    @Override
    @Test
    public void indirectSaveRequiredInNewTraThrow() throws Exception {
        super.indirectSaveRequiredInNewTraThrow();
    }

    @Override
    @Test
    public void indirectSaveNewInNewTraThrow() throws Exception {
        super.indirectSaveNewInNewTraThrow();
    }

    @Override
    @Test
    public void indirectSaveRequiredInNewTra() throws Exception {
        super.indirectSaveRequiredInNewTra();
    }

    @Override
    @Test
    public void indirectSaveRequiredPlusNewInNewTra() throws Exception {
        super.indirectSaveRequiredPlusNewInNewTra();
    }

    @Override
    @Test
    public void indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrow() throws Exception {
        super.indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrow();
    }

    @Override
    @Test
    public void indirectSaveRequiresNewLocalAsBusinessObject() throws Exception {
        super.indirectSaveRequiresNewLocalAsBusinessObject();
    }

    @Override
    @Test
    public void indirectSaveRequiresNewLocalAsBusinessObjectAndThrow() throws Exception {
        super.indirectSaveRequiresNewLocalAsBusinessObjectAndThrow();
    }

    @Override
    @Test
    public void indirectSaveRequiresNewLocalUsingSelfAndThrow() throws Exception {
        super.indirectSaveRequiresNewLocalUsingSelfAndThrow();
    }

    @Override
    @Test
    public void testBeanManagedTransactionsInTra() throws Exception {
        super.testBeanManagedTransactionsInTra();
    }

    @Test
    public void testBeanManagedTransactionsInTraEncapsulatedPlusPreAndPostTras() throws Exception {
        userTransaction.begin();
        userTransaction.commit();
        super.testBeanManagedTransactionsInTra();
        userTransaction.begin();
        userTransaction.commit();
    }

    @Override
    @Test
    public void testBeanManagedTransactionsWOTra() throws Exception {
        Assertions.assertThrows(EJBException.class, () -> super.testBeanManagedTransactionsWOTra());
    }

    @Override
    @Test
    public void testBeanManagedTransactionsWOTraButOuter() throws Exception {
        Assertions.assertThrows(EJBException.class, () -> super.testBeanManagedTransactionsWOTraButOuter());
    }

    @Override
    public void testBeanManagedWOTraInTestCode() {
        Assertions.assertThrows(TransactionRequiredException.class, () -> super.testBeanManagedWOTraInTestCode());
    }

    @Override
    @Test
    public void testBeanManagedWithTraInTestCodeInSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        super.testBeanManagedWithTraInTestCodeInSupported();
    }

    @Override
    @Test
    public void tryTestBeanManagedWOTraInTestCodeInSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Assertions.assertThrows(EJBException.class, () -> super.tryTestBeanManagedWOTraInTestCodeInSupported());
    }

    @Override
    @Test
    public void testBeanManagedWithTraInTestCodeTryInNotSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Assertions.assertThrows(EJBException.class, () -> super.testBeanManagedWithTraInTestCodeTryInNotSupported());
    }

    @Override
    @Test
    public void testBeanManagedWOTraInTestCodeTryInNotSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Assertions.assertThrows(EJBException.class, () -> super.testBeanManagedWOTraInTestCodeTryInNotSupported());
    }

    @Override
    @Test
    public void saveToSetRollbackOnlyAndTryAdditionalSave() throws Exception {
        super.saveToSetRollbackOnlyAndTryAdditionalSave();
    }

    @Override
    @Test
    public void canInterpretTransactionAttributeInParentClass() throws Exception {
        super.canInterpretTransactionAttributeInParentClass();
    }

    @Override
    @Test
    public void canInterpretTransactionAttributeInParentMethodRequired() throws Exception {
        super.canInterpretTransactionAttributeInParentMethodRequired();
    }

    @Override
    @Test
    public void canInterpretTransactionAttributeInParentMethodNever() throws Exception {
        Assertions.assertThrows(EJBException.class, () -> super.canInterpretTransactionAttributeInParentMethodNever());
    }

    @Test
    public void testCacheWOTransaction() throws Exception {
        TestEntity1 entity1 = new TestEntity1();
        entity1.setIntAttribute(1);
        try (TestTransaction newTransaction = new TestTransaction(TransactionAttributeType.REQUIRES_NEW)) {
            entityManager.persist(entity1);
        }
        entity1 = entityManager.find(TestEntity1.class, entity1.getId());
        try (TestTransaction newTransaction = new TestTransaction(TransactionAttributeType.REQUIRES_NEW)) {
            TestEntity1 entity11 = entityManager.find(TestEntity1.class, entity1.getId());
            entity11.setIntAttribute(2);
        }
        TestEntity1 entity12 = entityManager.find(TestEntity1.class, entity1.getId());
        assertThat(entity12.getIntAttribute(), is(2));
    }


    @ApplicationScoped
    public static class TestDbPersistenceFactory extends SinglePersistenceFactory {

        @Produces
        @Override
        public EntityManager newEm() {
            return produceEntityManager();
        }

    }
}
