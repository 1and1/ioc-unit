package ejbcdiunit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunitejb.transactional.TransactionalTestBase;

/**
 * @author aschoerk
 */
@RunWith(Arquillian.class)
public class WildflyArquillianJtaTransactionalTest extends TransactionalTestBase {
    @Deployment
    public static Archive<?> createTestArchive() {
        return WildflyArquillianEjbTransactionTest.getWarFromTargetFolder();
    }

    @Before
    public void setup() throws Exception {
        testRunner.setUp();
    }

    @Test
    @Override
    public void canInsertInspiteOfRE() throws Exception {
        super.canInsertInspiteOfRE();
    }

    @Test
    @Override
    public void canInsertInspiteOfDerivedRE() throws Exception {
        super.canInsertInspiteOfDerivedRE();
    }

    @Test
    @Override
    public void canInsertInspiteOfRollbackOn() throws Exception {
        super.canInsertInspiteOfRollbackOn();
    }

    @Test
    @Override
    public void canInsertInspiteOfRollbackOnWithDerived() throws Exception {
        super.canInsertInspiteOfRollbackOnWithDerived();
    }

    @Test
    @Override
    public void canNotInsertWithRollbackon() throws Exception {
        super.canNotInsertWithRollbackon();
    }

    @Test
    @Override
    public void canNotInsertWithRollbackonDerived() throws Exception {
        super.canNotInsertWithRollbackonDerived();
    }

    @Test
    @Override
    public void doesNotInsertIfSetRollbackOnly() throws Exception {
        super.doesNotInsertIfSetRollbackOnly();
    }

}
