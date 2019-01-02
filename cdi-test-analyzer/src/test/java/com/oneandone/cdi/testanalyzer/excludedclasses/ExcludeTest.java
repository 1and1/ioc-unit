package com.oneandone.cdi.testanalyzer.excludedclasses;

import com.oneandone.cdi.testanalyzer.BaseTest;
import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;
import com.oneandone.cdi.testanalyzer.annotations.TestPackages;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToInclude;
import org.junit.BeforeClass;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author aschoerk
 */

public class ExcludeTest extends BaseTest {
    @TestPackages({ToInclude.class})
    @ExcludedClasses({ToExclude.class})
    static class Test {
        @Inject
        ToInclude toInclude;
        @Produces
        ToExclude.ToExcludeProduced tmp = new ToExclude.ToExcludeProduced(11); // no produces clash with excluded ToExclude
        @Inject
        ToExclude.ToExcludeProduced toExcludeProduced;
    }

    @BeforeClass
    public static void initToInclude() {
        ToInclude.count = 0;
    }
    @org.junit.Test
    public void test() {
        createTest(SubExcludeTest.Test.class);
    }
}
