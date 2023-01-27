package ejbcdiunit;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunitejb.testbases.ValidationTestDelegate;

/**
 * @author aschoerk
 */
@RunWith(Arquillian.class)
public class WildflyValidationTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return WildflyArquillianEjbTransactionTest.getWarFromTargetFolder();
    }

    Logger log = LoggerFactory.getLogger("WildflyValidationTest");

    @Inject
    ValidationTestDelegate validationTest;



    @Test
    public void checkValidationInEjb() throws Exception {
        validationTest.checkValidationInEjb();
    }

    @Test
    public void checkValidationInNotSupported() throws Exception {
        validationTest.checkValidationInNotSupported();
    }

    @Test
    public void checkValidationAppScopedInTransaction() throws Exception {
        validationTest.checkValidationAppScopedInTransaction();
    }

    @Test
    public void checkValidationAppScopedWOTransaction() throws Exception {
        validationTest.checkValidationAppScopedWOTransaction();
    }
}
