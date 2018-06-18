package ejbcdiunit;

import static org.hamcrest.core.Is.is;

import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TransactionRequiredException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.SingletonBean;
import org.apache.openejb.jee.StatelessBean;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.junit.ApplicationComposerRule;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.ejbcdiunit.ejbs.CDIClass;
import com.oneandone.ejbcdiunit.ejbs.OuterClass;
import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessBeanManagedTrasEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessChildEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.jpa.TomeeResources;
import com.oneandone.ejbcdiunit.testbases.EJBTransactionTestBase;
import com.oneandone.ejbcdiunit.testbases.TestEntity1Saver;

public class EmbeddedTomeeTransactionTest extends EJBTransactionTestBase {

    @Rule
    public ApplicationComposerRule applicationComposerRule = new ApplicationComposerRule(this);

    @EJB
    SingletonEJB singletonEJB;

    @EJB(name = "shsgdhasghdasg") // tomee does not care about those names
    StatelessEJB statelessEJB;

    @EJB
    protected StatelessChildEJB statelessChildEJB;


    @Resource
    UserTransaction userTransaction;
    @Produces
    @PersistenceContext(unitName = "test-unit", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Module
    public PersistenceUnit persistence1() {
        return persistence("test-unit");
    }
    @Module
    public PersistenceUnit persistence2() {
        return persistence("EjbTestPU");
    }
    @Module
    public PersistenceUnit persistence3() {
        return persistence("EjbTestPUOperating");
    }

    public PersistenceUnit persistence(String name) {
        PersistenceUnit unit = new PersistenceUnit(name);
        unit.setJtaDataSource(name + "Database");
        unit.setNonJtaDataSource(name + "DatabaseUnmanaged");
        unit.getClazz().add(TestEntity1.class.getName());
        unit.setProperty("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
        return unit;
    }

    @Before
    public void beforeEmbeddedTomeeTransactionTest()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        entityManager.createQuery("delete from TestEntity1 e").executeUpdate();
        userTransaction.commit();
    }

    @Module
    public EjbModule module() {
        EjbModule module = new EjbModule(new EjbJar("test-beans")
                .enterpriseBean(new StatelessBean(StatelessChildEJB.class))
                .enterpriseBean(new StatelessBean(StatelessEJB.class))
                .enterpriseBean(new StatelessBean(OuterClass.class))
                .enterpriseBean(new StatelessBean(StatelessBeanManagedTrasEJB.class))
                .enterpriseBean(new SingletonBean(SingletonEJB.class))
                );
        Beans beans = new Beans();
        beans
                .managedClass(TomeeResources.class.getName())
                .managedClass(CDIClass.class.getName());
        module.setBeans(beans);
        module.setModuleId("test-module");
        return module;
    }


    @Configuration
    public Properties config() throws Exception {
        Properties p = new Properties();
        p.put("testDatabase", "new://Resource?type=DataSource");
        p.put("testDatabase.JdbcDriver", "org.h2.Driver");
        p.put("testDatabase.JdbcUrl", "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0");
        return p;
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
            }
            catch (RuntimeException r) {
                exceptionHappened = true;
                if (exceptionExpected != exceptionHappened) {
                    logger.error("Exception not expected",r);
                }
                logger.info("TransactionStatus: {}", userTransaction.getStatus());
                if (userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    userTransaction.rollback();
                }
                if (userTransaction.getStatus() != Status.STATUS_ACTIVE) {
                    userTransaction.begin();
                }

            }
            Assert.assertThat(exceptionHappened, is(exceptionExpected));
            TestEntity1 entity = new TestEntity1();
            entityManager.persist(entity);
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

    @Override
    @Test
    public void indirectSaveRequiresNewLocalUsingSelfAndThrow() throws Exception {
        super.indirectSaveRequiresNewLocalUsingSelfAndThrow();
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

        singletonEJB.methodCallUsingSessionContext();

        statelessEJB.method1();


        TestEntity1 testEntity1 = new TestEntity1();
        testEntity1.setIntAttribute(11);
        testEntity1.setStringAttribute("string");
        statelessEJB.saveInCurrentTransaction(testEntity1);

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

}