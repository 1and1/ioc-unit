package com.oneandone.iocunit.analyzer.excludedclasses;


import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;

/**
 * @author aschoerk
 */

public class SubExcludeTest extends BaseTest {

    @ExcludedClasses({ ExcludeTest.class })
    static class Test extends AbstractExcludeTestHolder.AbstractExcludeTest {

    }

    @org.junit.jupiter.api.Test
    public void test() {
        createTest(Test.class);
    }
}
