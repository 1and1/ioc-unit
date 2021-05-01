package com.oneandone.iocunit.analyzer.excludedclasses;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.BeforeClass;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */

public class ExcludeTest extends BaseTest {
    @TestPackages({ToInclude.class})
    @ExcludedClasses({ToExclude.class})
    static class Test extends BaseClass {
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

    @org.junit.jupiter.api.Test
    public void test() {
        createTest(SubExcludeTest.Test.class);
    }
}
