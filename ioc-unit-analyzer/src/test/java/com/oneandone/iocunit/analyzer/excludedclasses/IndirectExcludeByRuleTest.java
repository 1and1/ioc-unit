package com.oneandone.iocunit.analyzer.excludedclasses;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToExclude;

import javax.inject.Inject;

/**
 * @author aschoerk
 */

public class IndirectExcludeByRuleTest extends BaseTest {
    @TestClasses({IndirectExcluding.class})
    @SutClasses({ToExclude.class})
    static class Test {
        @Inject
        ToExclude toExclude;
    }
    @org.junit.Test
    public void test() {
        createTest(SubExcludeTest.Test.class);
    }

}
