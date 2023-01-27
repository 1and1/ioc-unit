package com.oneandone.iocunitejb.testbases;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import org.slf4j.Logger;

import com.oneandone.iocunitejb.ejbs.CDIClass;
import com.oneandone.iocunitejb.ejbs.OuterClass;
import com.oneandone.iocunitejb.ejbs.OuterClassUsingNonEjbTransactional;
import com.oneandone.iocunitejb.ejbs.SingletonEJB;
import com.oneandone.iocunitejb.ejbs.StatelessBeanManagedTrasEJB;
import com.oneandone.iocunitejb.ejbs.StatelessChildEJB;
import com.oneandone.iocunitejb.ejbs.StatelessEJB;
import com.oneandone.iocunitejb.ejbs.TransactionalApplicationScoped;
import com.oneandone.iocunitejb.entities.TestEntity1;

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
    protected OuterClassUsingNonEjbTransactional outerClassUsingNonEjbTransactional;

    @Inject
    protected TransactionalApplicationScoped transactionalApplicationScoped;

    @Inject
    protected EntityManager entityManager;

    @Inject
    protected UserTransaction userTransaction;

    protected void runTestInRolledBackTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {
        throw new RuntimeException("Should never be called in BaseClass.");
    }

    protected void runTestWithoutTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {
        throw new RuntimeException("Should never be called in BaseClass.");
    }


    /**
     * check if Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void requiresNewMethodWorks() throws Exception {
        runTestInRolledBackTransaction(e -> statelessEJB.saveInNewTransaction(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void requiresNewMethodWorksNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> transactionalApplicationScoped.saveInNewTransaction(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if call without Annotation,, means REQUIRED, works
     * @throws Exception don_t care
     */
    public void defaultMethodWorks() throws Exception {
        runTestWithoutTransaction(e -> statelessEJB.saveInCurrentTransactionDefaultTraAttribute(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
        runTestInRolledBackTransaction(e -> statelessEJB.saveInCurrentTransactionDefaultTraAttribute(e), 3, false);
        res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if call without Annotation,, means REQUIRED, works
     * @throws Exception don_t care
     */
    public void defaultMethodWorksNonEjb() throws Exception {
        runTestWithoutTransaction(e -> transactionalApplicationScoped.saveInCurrentTransactionDefaultTraAttribute(e), 1, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
        runTestInRolledBackTransaction(e -> transactionalApplicationScoped.saveInCurrentTransactionDefaultTraAttribute(e), 2, false);
        res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    private Number getNumberOfTestEntity() {
        return entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
    }

    /**
     * check if Required-Annotation works
     * @throws Exception don_t care
     */
    public void requiredMethodWorks() throws Exception {
        runTestInRolledBackTransaction(e -> statelessEJB.saveInCurrentTransaction(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if Required-Annotation works
     * @throws Exception don_t care
     */
    public void requiredMethodWorksNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> transactionalApplicationScoped.saveInCurrentTransaction(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if nested Requires_New-Annotation in Required works
     * @throws Exception don_t care
     */
    public void indirectSaveNewInRequired() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveNewInRequired(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if nested Requires_New-Annotation in Required works
     * @throws Exception don_t care
     */
    public void indirectSaveNewInRequiredNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveNewInRequired(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }



    /**
     * check if Requires_New-Annotation works even if afterwards a Runtime-Exception is called
     * @throws Exception don_t care
     */
    public void indirectSaveNewInRequiredThrowException() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveNewInRequiredThrowRTException(e), 2, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Requires_New-Annotation works even if afterwards a Runtime-Exception is called
     * @throws Exception don_t care
     */
    public void indirectSaveNewInRequiredThrowExceptionNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveNewInRequiredThrowRTException(e), 2, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Required nested in Required-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInRequired() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredInRequired(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if Required nested in Required-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInRequiredNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveRequiredInRequired(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if Required nested in Required-Annotation works where the outer RuntimeException should lead to rollback of
     * everything.
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInRequiredThrowException() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredInRequiredThrowException(e), 1, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if Required nested in Required-Annotation works where the outer RuntimeException should lead to rollback of
     * everything.
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInRequiredThrowExceptionNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveRequiredInRequiredThrowException(e), 1, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }


    /**
     * check if Requires_new nested in Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveNewInNewTra() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveNewInNewTra(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Requires_new nested in Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveNewInNewTraNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveNewInNewTra(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }
    /**
     * check if Required nested in Requires_New-Annotation works here the outer RuntimeException leads to rollback
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInNewTraThrow() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredInNewTraThrow(e), 1, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if Required nested in Requires_New-Annotation works here the outer RuntimeException leads to rollback
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInNewTraThrowNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveRequiredInNewTraThrow(e), 1, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }
    /**
     * check if Requires_New nested in Requires_New-Annotation works, where the outer RuntimeException therefore does
     * not rollback the nested changes.
     * @throws Exception don_t care
     */
    public void indirectSaveNewInNewTraThrow() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveNewInNewTraThrow(e), 2, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Requires_New nested in Requires_New-Annotation works, where the outer RuntimeException therefore does
     * not rollback the nested changes.
     * @throws Exception don_t care
     */
    public void indirectSaveNewInNewTraThrowNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveNewInNewTraThrow(e), 2, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Required nested in Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInNewTra() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredInNewTra(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if Required nested in Requires_New-Annotation works
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredInNewTraNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveRequiredInNewTra(e), 2, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }
    /**
     * check if Requires_new nested in Required nested in Requires_New-Annotation works.
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredPlusNewInNewTra() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiredPlusNewInNewTra(e), 3, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(2));
    }

    /**
     * check if Requires_new nested in Required nested in Requires_New-Annotation works.
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredPlusNewInNewTraNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveRequiredPlusNewInNewTra(e), 3, false);
        Number res = getNumberOfTestEntity();
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
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if Requires_new nested in Required by direct call nested in Requires_New-Annotation works,
     * whereas the outer RT-Exception rolls back the outer two transactions. The innerst Requires_New is ignore because
     * of the direct call.
     *
     * @throws Exception don_t care
     */
    public void indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrowNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveRequiredPlusNewInNewTraButDirectCallAndThrow(e), 1, true);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(0));
    }

    /**
     * check if indirect call via BusinessInterface returned from SessionContext works
     *
     * @throws Exception don_t care
     */
    public void indirectSaveRequiresNewLocalAsBusinessObject() throws Exception {
        runTestInRolledBackTransaction(e -> outerClass.saveRequiresNewLocalAsBusinessObject(e), 3, false);
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(2));
    }
    /**
     * check if indirect call via BusinessInterface returned from SessionContext works
     *
     * @throws Exception don_t care
     */
    public void indirectSaveRequiresNewLocalAsBusinessObjectNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveRequiresNewLocalAsBusinessObject(e), 3, false);
        Number res = getNumberOfTestEntity();
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
        Number res = getNumberOfTestEntity();
        assertThat(res.intValue(), is(1));
    }

    /**
     * check if indirect call via BusinessInterface returned from SessionContext works and subsequent RuntimeException
     * therefore does not lead to rollback of inner insert.
     *
     * @throws Exception don_t care
     */
    public void indirectSaveRequiresNewLocalAsBusinessObjectAndThrowNonEjb() throws Exception {
        runTestInRolledBackTransaction(e -> outerClassUsingNonEjbTransactional.saveRequiresNewLocalAsBusinessObjectAndThrow(e), 2, true);
        Number res = getNumberOfTestEntity();
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
        Number res = getNumberOfTestEntity();
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
        Number res = getNumberOfTestEntity();
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
