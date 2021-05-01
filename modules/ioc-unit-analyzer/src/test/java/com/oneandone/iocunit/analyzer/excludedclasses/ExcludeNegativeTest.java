package com.oneandone.iocunit.analyzer.excludedclasses;

import javax.inject.Inject;

import org.junit.BeforeClass;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */

public class ExcludeNegativeTest extends BaseTest {

    @TestPackages({ToInclude.class})
    static class Test extends BaseClass {
        @Inject
        ToInclude toInclude;
        @Inject
        ToExclude.ToExcludeProduced toExcludeProduced; // produced by not excluded ToExclude

    }

    @BeforeClass
    public static void initToInclude() {
        ToInclude.count = 0;
    }

    @org.junit.jupiter.api.Test
    public void test() {

        createTest(Test.class);
    }
}
