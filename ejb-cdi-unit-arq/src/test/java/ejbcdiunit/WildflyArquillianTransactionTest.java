package ejbcdiunit;

import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.ejb.EJBException;
import javax.persistence.TransactionRequiredException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.testbases.EJBTransactionTestBase;
import com.oneandone.ejbcdiunit.testbases.TestEntity1Saver;

@RunWith(Arquillian.class)
public class WildflyArquillianTransactionTest extends EJBTransactionTestBase {

    public static WebArchive getWarFromTargetFolder() {
        File folder = new File("../ejb-cdi-unit-test-war/target/");
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".war");
            }
        });
        if(files == null) {
            throw new IllegalArgumentException("Could not find directory " + folder.toString());
        } else if(files.length != 1) {
            throw new IllegalArgumentException("Exactly 1 war file expected, but found " + Arrays.toString(files));
        } else {
            WebArchive war = (WebArchive)ShrinkWrap.createFromZipFile(WebArchive.class, files[0]);
            return war;
        }
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return getWarFromTargetFolder();
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

    @Before
    public void setup() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        entityManager.createQuery("delete from TestEntity1 e").executeUpdate();
        userTransaction.commit();
    }

    @Test
    public void test1() throws Exception {
        runTestInRolledBackTransaction(e -> statelessEJB.saveInNewTransaction(e), 2, false);
        checkEntityNumber(1);
    }


    @Test
    public void firstTraTest() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        statelessEJB.saveInNewTransaction(new TestEntity1());
        userTransaction.rollback();

        final int expected = 1;
        checkEntityNumber(expected);
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

}