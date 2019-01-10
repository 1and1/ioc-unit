package com.oneandone.cdi.testanalyzer.excludedclasses;


import com.oneandone.cdi.testanalyzer.BaseTest;
import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;

/**
 * @author aschoerk
 */

public class SubExcludeTest extends BaseTest {

    @ExcludedClasses({ ExcludeTest.class })
    static class Test extends AbstractExcludeTestHolder.AbstractExcludeTest {

    }

    @org.junit.Test
    public void test() {
        createTest(Test.class);
    }
}
