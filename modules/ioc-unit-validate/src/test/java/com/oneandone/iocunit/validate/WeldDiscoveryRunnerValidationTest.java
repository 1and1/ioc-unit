package com.oneandone.iocunit.validate;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.discoveryrunner.WeldDiscoveryRunner;
import com.oneandone.cdi.discoveryrunner.annotations.TestClasses;


/**
 * @author aschoerk
 */
@RunWith(WeldDiscoveryRunner.class)
// @ValidateClasses(Sut1.class)
@TestClasses({WeldDiscoveryRunnerValidationTest.class, Sut1.class})
// @TestClasses(ValidationExtension.class)
public class WeldDiscoveryRunnerValidationTest {

    @Inject
    Sut1 sut1;

    @Test(expected = ConstraintViolationException.class)
    public void test() {
        if(ValidationClassFinder.getConstructorValidatedAnnotation() == null) {
            throw new ConstraintViolationException("Fake exception", null);
        }
        sut1.method1(null);
    }
}
