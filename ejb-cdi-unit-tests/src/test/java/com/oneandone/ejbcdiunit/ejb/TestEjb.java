package com.oneandone.ejbcdiunit.ejb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.ejb.EJBException;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;

import com.oneandone.ejbcdiunit.cdiunit.EjbJarClasspath;
import com.oneandone.ejbcdiunit.ejbs.appexc.TestBaseClass;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.ejbcdiunit.EjbUnitRule;
import com.oneandone.ejbcdiunit.SessionContextFactory;
import com.oneandone.ejbcdiunit.ejbs.MdbEjbInfoSingleton;
import com.oneandone.ejbcdiunit.ejbs.QMdbEjb;
import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessBeanManagedTrasEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessChildEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.helpers.LoggerGenerator;
import com.oneandone.ejbcdiunit.persistence.SinglePersistenceFactory;
import com.oneandone.ejbcdiunit.persistence.TestTransaction;
import com.oneandone.ejbcdiunit.testbases.EJBTransactionTestBase;
import com.oneandone.ejbcdiunit.testbases.TestEntity1Saver;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
@AdditionalClasses({ StatelessEJB.class, SingletonEJB.class,
        TestEjb.TestDbPersistenceFactory.class, SessionContextFactory.class,
        StatelessBeanManagedTrasEJB.class, StatelessChildEJB.class,
        QMdbEjb.class, MdbEjbInfoSingleton.class, LoggerGenerator.class })
@EjbJarClasspath(TestBaseClass.class)
public class TestEjb extends EJBTransactionTestBase {

    @Inject
    SinglePersistenceFactory persistenceFactory;

    @AfterClass
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

    @Rule
    public EjbUnitRule getEjbUnitRule() {
        return new EjbUnitRule(this);
    }

    @Before
    public void setupProfiler() throws InterruptedException {
        initMemory();
        System.getProperties().setProperty("hibernate.show_sql", "true");
    }

    @Test
    public void everythingNotNull() {
        Assert.assertNotNull(cdiClass);
        Assert.assertNotNull(cdiClass.getSingletonEJB());
        Assert.assertNotNull(cdiClass.getStatelessEJB());
        cdiClass.getStatelessEJB().method1();
        cdiClass.getSingletonEJB().methodCallUsingSessionContext();
        cdiClass.getSingletonEJB().methodCallUsingSelf();
        cdiClass.doSomething();
        Assert.assertNotNull(statelessEJB);
        Assert.assertNotNull(singletonEJB);
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
            Assert.assertThat(exceptionHappened, is(exceptionExpected));
            entityManager.persist(new TestEntity1());
            entityManager.flush();
            Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
            Assert.assertThat(res.intValue(), is(num));
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
    @Test(expected = EJBException.class)
    public void testBeanManagedTransactionsWOTra() throws Exception {
        super.testBeanManagedTransactionsWOTra();
    }

    @Override
    @Test(expected = EJBException.class)
    public void testBeanManagedTransactionsWOTraButOuter() throws Exception {
        super.testBeanManagedTransactionsWOTraButOuter();
    }

    @Override
    @Test(expected = TransactionRequiredException.class)
    public void testBeanManagedWOTraInTestCode() {
        super.testBeanManagedWOTraInTestCode();
    }

    @Override
    @Test
    public void testBeanManagedWithTraInTestCodeInSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        super.testBeanManagedWithTraInTestCodeInSupported();
    }

    @Override
    @Test(expected = EJBException.class)
    public void tryTestBeanManagedWOTraInTestCodeInSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        super.tryTestBeanManagedWOTraInTestCodeInSupported();
    }

    @Override
    @Test(expected = EJBException.class)
    public void testBeanManagedWithTraInTestCodeTryInNotSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        super.testBeanManagedWithTraInTestCodeTryInNotSupported();
    }

    @Override
    @Test(expected = EJBException.class)
    public void testBeanManagedWOTraInTestCodeTryInNotSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        super.testBeanManagedWOTraInTestCodeTryInNotSupported();
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
    @Test(expected = EJBException.class)
    public void canInterpretTransactionAttributeInParentMethodNever() throws Exception {
        super.canInterpretTransactionAttributeInParentMethodNever();
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
