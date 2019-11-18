package com.oneandone.iocunit.validate;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@ValidateClasses(Sut1.class)
@SutClasses(Sut1.class)
public class ValidationTest {

    @Inject
    Sut1 sut1;

    @Test(expected = ConstraintViolationException.class)
    public void test() {
        sut1.method1(null);
    }
}
