package ejbcdiunit;

import static org.hamcrest.core.Is.is;

import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.testbases.EJBTransactionTestBase;
import com.oneandone.ejbcdiunit.testbases.TestEntity1Saver;

@RunWith(JUnit4.class)
public class ServiceTest extends EJBTransactionTestBase {

    @Produces
    @PersistenceContext(unitName = "test-unit", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @EJB
    SingletonEJB singletonEJB;

    @Inject
    StatelessEJB statelessEJB;

    @Resource
    UserTransaction userTransaction;

    static EJBContainer container;

    Context context;

    @Before
    public void beforeServiceTest() throws NamingException, SystemException, NotSupportedException {
        final Properties p = new Properties();

        p.put("exampleDS", "new://Resource?type=DataSource");
        p.put("exampleDS.JdbcDriver", "org.h2.Driver");
        p.put("exampleDS.JdbcUrl", "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0");

        container = EJBContainer.createEJBContainer(p);
        context = container.getContext();
        context.bind("inject", this);
        entityManager.getDelegate();   // initiate initial creation of CDI-Bean behind.
    }

    @After
    public void afterServiceTest() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SQLException {
        container.close();
    }

    @Override
    public void runTestInRolledBackTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {

        logger.info("runTestInRolledBackTransaction for arquillian: num: {} exceptionExpected {}",num, exceptionExpected);
        userTransaction.begin();
        try {
            TestEntity1 testEntity1 = new TestEntity1();
            boolean exceptionHappened = false;
            try {
                saver.save(testEntity1);
                logger.info("first entity: {}", testEntity1.getId());
            }
            catch (RuntimeException r) {
                exceptionHappened = true;
                logger.info("TransactionStatus: {}", userTransaction.getStatus());
                if (userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    userTransaction.rollback();
                }
                if (userTransaction.getStatus() != Status.STATUS_ACTIVE) {
                    userTransaction.begin();
                }

            }
            Assert.assertThat(exceptionHappened, is(exceptionExpected));
            final TestEntity1 entity2 = new TestEntity1();
            entityManager.persist(entity2);
            entityManager.flush();
            logger.info("second entity: {}", entity2.getId());
            checkEntityNumber(num);
        }
        finally {
            userTransaction.rollback();
        }
    }

    private void checkEntityNumber(int expected) {
        Number res = entityManager.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        Assert.assertThat(res.intValue(), is(expected));
    }

    @Test
    public void requiresNewMethodWorks() throws Exception {
        super.requiresNewMethodWorks();
    }

    @Test
    public void defaultMethodWorks() throws Exception {
        super.defaultMethodWorks();
    }

    @Test
    public void requiredMethodWorks() throws Exception {
        super.requiredMethodWorks();
    }

    @Test
    public void indirectSaveNewInRequired() throws Exception {
        super.indirectSaveNewInRequired();
    }

    @Test
    public void indirectSaveNewInRequiredThrowException() throws Exception {
        super.indirectSaveNewInRequiredThrowException();
    }

    @Test
    public void indirectSaveRequiredInRequired() throws Exception {
        super.indirectSaveRequiredInRequired();
    }

    @Test
    public void indirectSaveRequiredInRequiredThrowException() throws Exception {
        super.indirectSaveRequiredInRequiredThrowException();
    }

    @Test
    public void indirectSaveNewInNewTra() throws Exception {
        super.indirectSaveNewInNewTra();
    }

    @Test
    public void indirectSaveRequiredInNewTraThrow() throws Exception {
        super.indirectSaveRequiredInNewTraThrow();
    }

    @Test
    public void indirectSaveNewInNewTraThrow() throws Exception {
        super.indirectSaveNewInNewTraThrow();
    }

    @Test
    public void indirectSaveRequiredInNewTra() throws Exception {
        super.indirectSaveRequiredInNewTra();
    }

    @Test
    public void indirectSaveRequiredPlusNewInNewTra() throws Exception {
        super.indirectSaveRequiredPlusNewInNewTra();
    }

    @Test
    public void indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrow() throws Exception {
        super.indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrow();
    }

    @Test
    public void indirectSaveRequiresNewLocalAsBusinessObject() throws Exception {
        super.indirectSaveRequiresNewLocalAsBusinessObject();
    }

    @Test
    public void indirectSaveRequiresNewLocalAsBusinessObjectAndThrow() throws Exception {
        super.indirectSaveRequiresNewLocalAsBusinessObjectAndThrow();
    }

    @Test
    public void testBeanManagedTransactionsInTra() throws Exception {
        super.testBeanManagedTransactionsInTra();
    }

    @Override
    @Test(expected = EJBException.class)
    public void testBeanManagedTransactionsWOTraButOuter() throws Exception {
        super.testBeanManagedTransactionsWOTraButOuter();
    }

    @Override
    @Test(expected = EJBException.class)
    public void testBeanManagedTransactionsWOTra() throws Exception {
        super.testBeanManagedTransactionsWOTra();
    }

    @Test
    public void test2() throws Exception {
        // Movies movies = (Movies) context.lookup("java:global/ejb-cdi-unit-tomee/Movies");

        singletonEJB.method1();

        statelessEJB.method1();


        TestEntity1 testEntity1 = new TestEntity1();
        testEntity1.setIntAttribute(11);
        testEntity1.setStringAttribute("string");
        statelessEJB.saveInCurrentTransaction(testEntity1);

    }

}