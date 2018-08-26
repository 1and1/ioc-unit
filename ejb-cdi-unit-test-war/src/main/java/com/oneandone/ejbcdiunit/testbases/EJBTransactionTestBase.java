package com.oneandone.ejbcdiunit.testbases;

import com.oneandone.ejbcdiunit.ejbs.*;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author aschoerk
 */
@ApplicationScoped
public abstract class EJBTransactionTestBase {

    @Inject
    protected Logger logger;

    @Inject
    protected CDIClass cdiClass;

    @EJB(name = "StatelessEJB")
    protected StatelessEJB statelessEJB;

    @EJB
    protected SingletonEJB singletonEJB;

    @EJB
    protected StatelessChildEJB statelessChildEJB;

    @EJB
    protected StatelessBeanManagedTrasEJB statelessBeanManagedTrasEJB;

    @Inject
    protected OuterClass outerClass;

    @Inject
    protected EntityManager entityManager;

    @Inject
    protected UserTransaction userTransaction;



    protected void runTestInRolledBackTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {
        throw new RuntimeException("Should never be called in BaseClass.");
    }

    /**
     * check if Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void requiresNewMethodWorks() throws Exception {
        runTestInRolledBackTransaction(e -> statelessEJB.saveInNewTransaction(e), 2, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if call without Annotation,, means REQUIRED, works
     * @throws Exception don_t care
     */
    public void defaultMethodWorks() throws Exception {
        runTestInRolledBackTransaction(e -> statelessEJB.saveInCurrentTransactionDefaultTraAttribute(e), 2, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(0));
    }


    /**
     * check if Required-Annotation works
     * @throws Exception don_t care
     */
    public void requiredMethodWorks() throws Exception {
        runTestInRolledBackTransaction(e -> statelessEJB.saveInCurrentTransaction(e), 2, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if nested Requires_New-Annotation in Required works
     * @throws Exception don_t care
     */
    public void indirectSaveNewInRequired() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveNewInRequired(e), 2, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(1));
    }


    /**
     * check if Requires_New-Annotation works even if afterwards a Runtime-Exception is called
     * @throws Exception don_t care
     */
    public void indirectSaveNewInRequiredThrowException() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveNewInRequiredThrowRTException(e), 2, true);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(1));
    }


    /**
     * check if Required nested in Required-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInRequired() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredInRequired(e), 2, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if Required nested in Required-Annotation works where the outer RuntimeException should lead to rollback of
     * everything.
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInRequiredThrowException() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredInRequiredThrowException(e), 1, true);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(0));
    }


    /**
     * check if Requires_new nested in Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveNewInNewTra() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveNewInNewTra(e), 2, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Required nested in Requires_New-Annotation works here the outer RuntimeException leads to rollback
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInNewTraThrow() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredInNewTraThrow(e), 1, true);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(0));
    }


    /**
     * check if Requires_New nested in Requires_New-Annotation works, where the outer RuntimeException therefore does
     * not rollback the nested changes.
     * @throws Exception don_t care
     */
    public void indirectSaveNewInNewTraThrow() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveNewInNewTraThrow(e), 2, true);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Required nested in Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInNewTra() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredInNewTra(e), 2, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(1));
    }


    /**
     * check if Requires_new nested in Required nested in Requires_New-Annotation works.
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredPlusNewInNewTra() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredPlusNewInNewTra(e), 3, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(2));
    }

    /**
     * check if Requires_new nested in Required by direct call nested in Requires_New-Annotation works,
     * whereas the outer RT-Exception rolls back the outer two transactions. The innerst Requires_New is ignore because
     * of the direct call.
     *
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrow() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredPlusNewInNewTraButDirectCallAndThrow(e), 1, true);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if indirect call via BusinessInterface returned from SessionContext works
     *
     * @throws Exception don_t care
     */
    public void indirectSaveRequiresNewLocalAsBusinessObject() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiresNewLocalAsBusinessObject(e), 3, false);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(2));
    }

    /**
     * check if indirect call via BusinessInterface returned from SessionContext works and subsequent RuntimeException
     * therefore does not lead to rollback of inner insert.
     *
     * @throws Exception don_t care
     */
    public void indirectSaveRequiresNewLocalAsBusinessObjectAndThrow() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiresNewLocalAsBusinessObjectAndThrow(e), 2, true);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if indirect call via BusinessInterface returned from SessionContext works and subsequent RuntimeException therefore does not lead to
     * rollback of inner insert.
     *
     * @throws Exception
     *             don_t care
     */
    public void indirectSaveRequiresNewLocalUsingSelfAndThrow() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiresNewLocalUsingSelfAndThrow(e), 2, true);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(1));
    }


    /**
     * Check if Bean Managed Transaction Handling is done correctly.
     *
     * @throws Exception don't care
     */
    public void testBeanManagedTransactionsInTra() throws Exception {
        statelessBeanManagedTrasEJB.insertNewInTra(new TestEntity1());
    }

    /**
     * Check if Bean Managed Transaction Handling is done correctly.
     *
     * @throws Exception don't care
     */
    public void testBeanManagedTransactionsWOTraButOuter() throws Exception {
        userTransaction.begin();
        statelessBeanManagedTrasEJB.insertWithoutTra(new TestEntity1());
        userTransaction.commit();
    }

    /**
     * Check if Bean Managed Transaction Handling is done correctly.
     *
     * @throws Exception don't care
     */
    public void testBeanManagedTransactionsWOTra() throws Exception {
        statelessBeanManagedTrasEJB.insertWithoutTra(new TestEntity1());
    }


    public void testBeanManagedWOTraInTestCode() {
        TestEntity1 e = new TestEntity1();
        entityManager.persist(e);
    }

    public void testBeanManagedWithTraInTestCodeInSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        statelessEJB.saveInSupportedTransaction(new TestEntity1());
        userTransaction.commit();
    }

    public void tryTestBeanManagedWOTraInTestCodeInSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        statelessEJB.saveInSupportedTransaction(new TestEntity1());
    }


    public void testBeanManagedWithTraInTestCodeTryInNotSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        statelessEJB.trySaveInNotSupportedTransaction(new TestEntity1());
        userTransaction.commit();
    }

    public void testBeanManagedWOTraInTestCodeTryInNotSupported()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        statelessEJB.trySaveInNotSupportedTransaction(new TestEntity1());
        userTransaction.commit();
    }

    public void saveToSetRollbackOnlyAndTryAdditionalSave() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveToSetRollbackOnlyAndTryAdditionalSave(e), 1, true);
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(0));
    }



    public void canInterpretTransactionAttributeInParentClass() throws Exception {
        TestEntity1 entity1 = new TestEntity1();
        entity1.setIntAttribute(1);
        statelessChildEJB.saveInCurrentTransactionDefaultTraAttribute(entity1);
    }

    public void canInterpretTransactionAttributeInParentMethodRequired() throws Exception {
        userTransaction.begin();
        TestEntity1 entity1 = new TestEntity1();
        entity1.setIntAttribute(1);
        statelessChildEJB.saveInCurrentTransactionRequiredTraAttribute(entity1);
        userTransaction.commit();
    }

    public void canInterpretTransactionAttributeInParentMethodNever() throws Exception {
        userTransaction.begin();
        TestEntity1 entity1 = new TestEntity1();
        entity1.setIntAttribute(1);
        statelessChildEJB.saveInCurrentTransactionNeverTraAttribute(entity1);
        userTransaction.commit();
    }


}
