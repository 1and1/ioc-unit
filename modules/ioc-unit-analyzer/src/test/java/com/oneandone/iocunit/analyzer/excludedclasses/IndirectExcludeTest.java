package com.oneandone.iocunit.analyzer.excludedclasses;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */

public class IndirectExcludeTest extends BaseTest {
    @TestClasses({IndirectExcluding.class, ToExclude.class})
    static class Test {
        @Inject
        ToExclude toExclude;
    }

    @org.junit.jupiter.api.Test
    public void test() {
        createTest(SubExcludeTest.Test.class);
    }
}
