package ejbcdiunit;

import static org.hamcrest.core.Is.is;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.junit.Assert;
import org.slf4j.Logger;

import com.oneandone.iocunitejb.ejbs.TestRunnerIntf;
import com.oneandone.iocunitejb.entities.TestEntity1;
import com.oneandone.iocunitejb.testbases.TestEntity1Saver;

/**
 * @author aschoerk
 */
public class TestRunnerArq implements TestRunnerIntf {

    @Inject
    protected Logger logger;

    @Inject
    protected EntityManager entityManager;

    @Inject
    protected UserTransaction userTransaction;

    @Override
    public void runTestInRolledBackTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {

        logger.info("runTestInRolledBackTransaction for arquillian: num: {} exceptionExpected {}",num, exceptionExpected);
        userTransaction.begin();
        try {
            runTestWithoutTransaction(saver,num,exceptionExpected);
        }
        finally {
            if(userTransaction.getStatus() == Status.STATUS_ACTIVE) {
                userTransaction.rollback();
            }
        }
    }

    @Override
    public void runTestWithoutTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {
        TestEntity1 testEntity1 = new TestEntity1();
        boolean exceptionHappened = false;
        try {
            saver.save(testEntity1);
        }
        catch (Throwable r) {
            exceptionHappened = true;
            if (exceptionExpected != exceptionHappened) {
                logger.error("Exception not expected",r);
            }
            logger.info("TransactionStatus: {}", userTransaction.getStatus());
            if (userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                userTransaction.rollback();
            }
        }
        try {
            if(userTransaction.getStatus() != Status.STATUS_ACTIVE) {
                userTransaction.begin();
            }
            Assert.assertThat(exceptionHappened, is(exceptionExpected));
            TestEntity1 entity = new TestEntity1();
            entityManager.persist(entity);
            checkEntityNumber(num);
        } finally {
            userTransaction.rollback();
        }
    }

    @Override
    public void setUp() throws Exception {
        userTransaction.begin();
        entityManager.createQuery("delete from TestEntity1 e").executeUpdate();
        userTransaction.commit();
    }

    private void checkEntityNumber(int expected) {
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        Assert.assertThat(res.intValue(), is(expected));
    }
}
