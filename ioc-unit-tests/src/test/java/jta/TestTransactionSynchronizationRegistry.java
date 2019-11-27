package jta;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({XmlLessPersistenceFactory.class})
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
}
