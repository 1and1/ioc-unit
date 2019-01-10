package com.oneandone.cdi.testanalyzer.excludedclasses;

import com.oneandone.cdi.testanalyzer.BaseTest;
import com.oneandone.cdi.testanalyzer.annotations.TestPackages;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToInclude;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author aschoerk
 */

public class ExcludeByRuleTest extends BaseTest {

    @TestPackages({ToInclude.class})
    static class Test {
        @Inject
        ToInclude toInclude;
        @Produces
        ToExclude.ToExcludeProduced tmp = new ToExclude.ToExcludeProduced(11); // no produces clash with excluded ToExclude
        @Inject
        ToExclude.ToExcludeProduced toExcludeProduced;
    }

    @org.junit.Test
    public void test() {
        createTest(SubExcludeTest.Test.class);
    }
}
