package validation;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.validate.ValidateClasses;
import com.oneandone.iocunitejb.testbases.ValidationTestDelegate;
import com.oneandone.iocunitejb.validation.ValidatedApplicationScoped;
import com.oneandone.iocunitejb.validation.ValidatedEjb;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@ValidateClasses({ValidatedEjb.class, ValidatedApplicationScoped.class})
@TestClasses(ValidationTestDelegate.class)
@SutClasses({ValidatedEjb.class, ValidatedApplicationScoped.class})
public class ValidationTest {
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
