package com.oneandone.cdi.testanalyzer.excludedclasses;

import com.oneandone.cdi.testanalyzer.BaseTest;
import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */

public class IndirectExcluding extends BaseTest {
    @ExcludedClasses({ToExclude.class})
    static class Test {

    }

    @org.junit.Test
    public void test() {
        createTest(Test.class);
    }
}
