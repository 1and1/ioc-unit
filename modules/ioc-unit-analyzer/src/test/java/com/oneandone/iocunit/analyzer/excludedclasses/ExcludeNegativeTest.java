package com.oneandone.iocunit.analyzer.excludedclasses;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToInclude;
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
