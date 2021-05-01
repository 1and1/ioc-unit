package com.oneandone.iocunit.analyzer;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.test1.Test1A;
import com.oneandone.iocunit.analyzer.test1.Test1Interface;

/**
 * @author aschoerk
 */


public class TestSutPackage extends BaseTest {

    @SutPackages({Test1A.class})
    static class TestClass extends BaseClass {
        @Inject
        Test1A test1;

        @Inject
        Test1Interface test1Interface;
    }

    @Test
    public void test() {
        createTest(TestClass.class);
//        assertNotNull(test1);
//        assertEquals("Test1A", test1.call());
    }

    @Test
    public void canOptimizeInjecting() {
        createTest(TestClass.class);
//        assertNotNull(test1Interface);
//        assertEquals("Test1A", test1Interface.call());
    }
}
