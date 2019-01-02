package com.oneandone.cdi.testanalyzer.excludedclasses;

import com.oneandone.cdi.testanalyzer.BaseTest;
import com.oneandone.cdi.testanalyzer.annotations.TestPackages;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToInclude;
import org.junit.BeforeClass;

import javax.inject.Inject;

/**
 * @author aschoerk
 */

public class ExcludeNegativeTest extends BaseTest {

    @TestPackages({ToInclude.class})
    static class Test {
        @Inject
        ToInclude toInclude;
        @Inject
        ToExclude.ToExcludeProduced toExcludeProduced; // produced by not excluded ToExclude

    }

    @BeforeClass
    public static void initToInclude() {
        ToInclude.count = 0;
    }

    @org.junit.Test
    public void test() {

        createTest(Test.class);
    }
}
