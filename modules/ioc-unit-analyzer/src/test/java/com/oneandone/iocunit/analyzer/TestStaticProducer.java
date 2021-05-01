package com.oneandone.iocunit.analyzer;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.producing.ProducingClass2;
import com.oneandone.iocunit.analyzer.test2.Test2Interface;

/**
 * @author aschoerk
 */


public class TestStaticProducer extends BaseTest {

    @SutPackages({ProducingClass2.class})
    static class TestClass extends BaseClass {
        @Inject
        Test2Interface test2b;
    }


    @Test
    public void test() {
        createTest(TestClass.class);
//        Assertions.assertNotNull(test2b);
//        assertEquals("Test2B", test2b.call());
    }
}
