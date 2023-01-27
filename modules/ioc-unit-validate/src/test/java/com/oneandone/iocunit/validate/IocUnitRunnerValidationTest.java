package com.oneandone.iocunit.validate;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;


/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({Sut1.class, ValidationInitializer.class})
public class IocUnitRunnerValidationTest {

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
