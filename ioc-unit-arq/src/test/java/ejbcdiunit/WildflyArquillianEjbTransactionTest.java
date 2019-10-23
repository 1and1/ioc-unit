package ejbcdiunit;

import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.TransactionRequiredException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
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

import com.oneandone.iocunitejb.ejbs.TestRunnerIntf;
import com.oneandone.iocunitejb.entities.TestEntity1;
import com.oneandone.iocunitejb.testbases.EJBTransactionTestBase;
import com.oneandone.iocunitejb.testbases.TestEntity1Saver;

@RunWith(Arquillian.class)
public class WildflyArquillianEjbTransactionTest extends EJBTransactionTestBase {

    public static WebArchive getWarFromTargetFolder() {
        File folder = new File("../ioc-unit-test-war/target/");
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
            war.addClass(TestRunnerArq.class);
            return war;
        }
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return getWarFromTargetFolder();
    }

    @Inject
    TestRunnerIntf testRunner;

    @Override
    public void runTestInRolledBackTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {

        testRunner.runTestInRolledBackTransaction(saver, num, exceptionExpected);
    }

    @Override
    public void runTestWithoutTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception {
        testRunner.runTestWithoutTransaction(saver, num, exceptionExpected);
    }

    @Before
    public void setup() throws Exception {
        testRunner.setUp();
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

    @Override
    @Test
    public void requiresNewMethodWorks() throws Exception {
        super.requiresNewMethodWorks();
    }

    @Override
    @Test
    public void requiresNewMethodWorksNonEjb() throws Exception {
        super.requiresNewMethodWorksNonEjb();
    }

    @Override
    @Test
    public void defaultMethodWorks() throws Exception {
        super.defaultMethodWorks();
    }

    @Override
    @Test
    public void defaultMethodWorksNonEjb() throws Exception {
        super.defaultMethodWorksNonEjb();
    }

    @Override
    @Test
    public void requiredMethodWorks() throws Exception {
        super.requiredMethodWorks();
    }

    @Override
    @Test
    public void requiredMethodWorksNonEjb() throws Exception {
        super.requiredMethodWorksNonEjb();
    }

    @Override
    @Test
    public void indirectSaveNewInRequired() throws Exception {
        super.indirectSaveNewInRequired();
    }

    @Override
    @Test
    public void indirectSaveNewInRequiredNonEjb() throws Exception {
        super.indirectSaveNewInRequiredNonEjb();
    }

    @Override
    @Test
    public void indirectSaveNewInRequiredThrowException() throws Exception {
        super.indirectSaveNewInRequiredThrowException();
    }

    @Override
    @Test
    public void indirectSaveNewInRequiredThrowExceptionNonEjb() throws Exception {
        super.indirectSaveNewInRequiredThrowExceptionNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiredInRequired() throws Exception {
        super.indirectSaveRequiredInRequired();
    }

    @Override
    @Test
    public void indirectSaveRequiredInRequiredNonEjb() throws Exception {
        super.indirectSaveRequiredInRequiredNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiredInRequiredThrowException() throws Exception {
        super.indirectSaveRequiredInRequiredThrowException();
    }

    @Override
    @Test
    public void indirectSaveRequiredInRequiredThrowExceptionNonEjb() throws Exception {
        super.indirectSaveRequiredInRequiredThrowExceptionNonEjb();
    }

    @Override
    @Test
    public void indirectSaveNewInNewTra() throws Exception {
        super.indirectSaveNewInNewTra();
    }

    @Override
    @Test
    public void indirectSaveNewInNewTraNonEjb() throws Exception {
        super.indirectSaveNewInNewTraNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiredInNewTraThrow() throws Exception {
        super.indirectSaveRequiredInNewTraThrow();
    }

    @Override
    @Test
    public void indirectSaveRequiredInNewTraThrowNonEjb() throws Exception {
        super.indirectSaveRequiredInNewTraThrowNonEjb();
    }

    @Override
    @Test
    public void indirectSaveNewInNewTraThrow() throws Exception {
        super.indirectSaveNewInNewTraThrow();
    }

    @Override
    @Test
    public void indirectSaveNewInNewTraThrowNonEjb() throws Exception {
        super.indirectSaveNewInNewTraThrowNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiredInNewTra() throws Exception {
        super.indirectSaveRequiredInNewTra();
    }

    @Override
    @Test
    public void indirectSaveRequiredInNewTraNonEjb() throws Exception {
        super.indirectSaveRequiredInNewTraNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiredPlusNewInNewTra() throws Exception {
        super.indirectSaveRequiredPlusNewInNewTra();
    }

    @Override
    @Test
    public void indirectSaveRequiredPlusNewInNewTraNonEjb() throws Exception {
        super.indirectSaveRequiredPlusNewInNewTraNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrow() throws Exception {
        super.indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrow();
    }
    @Override
    @Test
    public void indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrowNonEjb() throws Exception {
        super.indirectSaveRequiredPlusNewInNewTraButDirectCallAndThrowNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiresNewLocalAsBusinessObject() throws Exception {
        super.indirectSaveRequiresNewLocalAsBusinessObject();
    }

    @Override
    @Test
    public void indirectSaveRequiresNewLocalAsBusinessObjectNonEjb() throws Exception {
        super.indirectSaveRequiresNewLocalAsBusinessObjectNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiresNewLocalAsBusinessObjectAndThrow() throws Exception {
        super.indirectSaveRequiresNewLocalAsBusinessObjectAndThrow();
    }

    @Override
    @Test
    public void indirectSaveRequiresNewLocalAsBusinessObjectAndThrowNonEjb() throws Exception {
        super.indirectSaveRequiresNewLocalAsBusinessObjectAndThrowNonEjb();
    }

    @Override
    @Test
    public void indirectSaveRequiresNewLocalUsingSelfAndThrow() throws Exception {
        super.indirectSaveRequiresNewLocalUsingSelfAndThrow();
    }

    @Override
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