package com.oneandone.cdi.testanalyzer.excludedclasses;

import com.oneandone.cdi.testanalyzer.BaseTest;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToExclude;

import javax.inject.Inject;

/**
 * @author aschoerk
 */

public class IndirectExcludeTest extends BaseTest {
    @TestClasses({IndirectExcluding.class, ToExclude.class})
    static class Test {
        @Inject
        ToExclude toExclude;
    }
    @org.junit.Test
    public void test() {
        createTest(SubExcludeTest.Test.class);
    }
}
