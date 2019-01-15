package com.oneandone.iocunit.analyzer.excludedclasses;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToExclude;

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
