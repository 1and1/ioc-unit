package com.oneandone.ejbcdiunit5.excludedclasses;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.tester.ExpectedStartupException;
import com.oneandone.cdi.tester.JUnit5Extension;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@SutClasses({ IndirectExcluding.class })
public class IndirectExcludeTest {

    @Test
    @ExpectedStartupException(RuntimeException.class)
    public void test() {
        throw new RuntimeException("should be hidden by StartupException");
    }
}
