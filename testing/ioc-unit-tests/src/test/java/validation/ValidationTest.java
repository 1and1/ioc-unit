package validation;

import jakarta.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.validate.ValidateClasses;
import com.oneandone.iocunit.validate.ValidationClassFinder;
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
        if(!hibernateValidator43x()) {
            validationTest.checkValidationInEjb();
        }
    }

    private boolean hibernateValidator43x() {
        Class c = ValidationClassFinder.getInterceptor();
        return c == null;
    }

    @Test
    public void checkValidationInNotSupported() throws Exception {
        if(!hibernateValidator43x()) validationTest.checkValidationInNotSupported();
    }

    @Test
    public void checkValidationAppScopedInTransaction() throws Exception {
        if(!hibernateValidator43x()) validationTest.checkValidationAppScopedInTransaction();
    }

    @Test
    public void checkValidationAppScopedWOTransaction() throws Exception {
        if(!hibernateValidator43x()) validationTest.checkValidationAppScopedWOTransaction();
    }
}
