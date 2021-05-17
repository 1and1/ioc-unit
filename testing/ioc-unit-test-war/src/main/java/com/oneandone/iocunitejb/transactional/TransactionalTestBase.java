package com.oneandone.iocunitejb.transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;

import com.oneandone.iocunitejb.ejbs.TestRunnerIntf;
import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
public abstract class TransactionalTestBase {
    @Inject
    protected Logger logger;

    @Inject
    protected UserTransaction userTransaction;

    @Inject
    protected EntityManager entityManager;

    @Inject
    protected TestRunnerIntf testRunner;

    @Inject
    protected ApplicationScopedTransactionalAllRequiresNew sut;


    private Number getNumberOfTestEntity() {
        return entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
    }

    protected void canJustInsert() throws Exception {
        testRunner.runTestInRolledBackTransaction(e -> sut.insertOnly(new TestEntity1()), 2, false);
    }

    protected void canInsertInspiteOfRE() throws Exception {
        testRunner.runTestInRolledBackTransaction(e -> sut.insertRE(new TestEntity1()), 2, true);
    }
    protected void canInsertInspiteOfDerivedRE() throws Exception {
        testRunner.runTestInRolledBackTransaction(e -> sut.insertDerivedRE(new TestEntity1()), 2, true);
    }
    protected void canInsertInspiteOfRollbackOn() throws Exception {
        testRunner.runTestInRolledBackTransaction(e -> sut.insertIllegalState(new TestEntity1()), 2, true);
    }
    protected void canInsertInspiteOfRollbackOnWithDerived() throws Exception {
        testRunner.runTestInRolledBackTransaction(e -> sut.insertDerivedIllegalState(new TestEntity1()), 2, true);
    }
    protected void canNotInsertWithRollbackon() throws Exception {
        testRunner.runTestInRolledBackTransaction(e -> sut.insertCheckedException(new TestEntity1()), 1, true);
    }
    protected void canNotInsertWithRollbackonDerived() throws Exception {
        testRunner.runTestInRolledBackTransaction(e -> sut.insertDerivedCheckedException(new TestEntity1()), 1, true);
    }

    protected void doesNotInsertIfSetRollbackOnly() throws Exception {
        testRunner.runTestInRolledBackTransaction(e -> sut.insertButSetRollbackOnly(new TestEntity1()), 1, false);
    }

}
