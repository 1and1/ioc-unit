package com.oneandone.iocunit.analyzer;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.producing.ProducingClass2;
import com.oneandone.iocunit.analyzer.test2.Test2Interface;

import org.junit.Test;

import javax.inject.Inject;

/**
 * @author aschoerk
 */


public class TestStaticProducer extends BaseTest {

    @SutPackages({ProducingClass2.class})
    static class TestClass {
        @Inject
        Test2Interface test2b;
    }


    @Test
    public void test() {
        createTest(TestClass.class);
//        Assert.assertNotNull(test2b);
//        assertEquals("Test2B", test2b.call());
    }
}
