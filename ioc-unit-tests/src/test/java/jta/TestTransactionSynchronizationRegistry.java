package jta;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;
import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({XmlLessPersistenceFactory.class})
@SutClasses(TestEntity1.class)
public class TestTransactionSynchronizationRegistry {

    @Inject
    UserTransaction userTransaction;

    @Inject
    TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @Test(expected = RollbackException.class)
    public void test() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        userTransaction.begin();
        transactionSynchronizationRegistry.setRollbackOnly();
        userTransaction.commit();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    static class TransactionTestClass {

        @Inject
        TransactionSynchronizationRegistry transactionSynchronizationRegistry;
        @Inject
        EntityManager em;

        public void doRollback() {
            TestEntity1 testEntity1 = new TestEntity1();
            em.persist(testEntity1);
            transactionSynchronizationRegistry.setRollbackOnly();
        }
    }

    @Inject
    TransactionTestClass transactionTestClass;

    @Test
    public void testWithoutUT() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        transactionTestClass.doRollback();
    }
}
