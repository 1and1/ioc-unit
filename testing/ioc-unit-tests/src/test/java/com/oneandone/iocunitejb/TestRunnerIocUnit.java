package com.oneandone.iocunitejb;

import static org.hamcrest.Matchers.is;

import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.junit.Assert;

import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;
import com.oneandone.iocunit.ejb.persistence.TestTransaction;
import com.oneandone.iocunitejb.ejbs.TestRunnerIntf;
import com.oneandone.iocunitejb.entities.TestEntity1;
import com.oneandone.iocunitejb.testbases.TestEntity1Saver;

/**
 * @author aschoerk
 */
public class TestRunnerIocUnit implements TestRunnerIntf {
    @Inject
    protected UserTransaction userTransaction;

    @Inject
    protected EntityManager entityManager;

    @Inject
    PersistenceFactory persistenceFactory;

    @Override
    public void runTestInRolledBackTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {
        try (TestTransaction resource1 = persistenceFactory.transaction(TransactionAttributeType.REQUIRES_NEW)) {
            TestEntity1 testEntity1 = new TestEntity1();
            boolean exceptionHappened = false;
            try {
                saver.save(testEntity1);
            } catch (Throwable r) {
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
    public void runTestWithoutTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {
        try (TestTransaction resource1 = persistenceFactory.transaction(TransactionAttributeType.NOT_SUPPORTED)) {
            TestEntity1 testEntity1 = new TestEntity1();
            boolean exceptionHappened = false;
            try {
                saver.save(testEntity1);
            } catch (RuntimeException r) {
                exceptionHappened = true;
                if (resource1.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    resource1.rollback();
                }
            }
            resource1.begin();
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
    public void setUp() throws Exception {

    }
}
