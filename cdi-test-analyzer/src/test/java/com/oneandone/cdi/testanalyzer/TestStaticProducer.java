package com.oneandone.cdi.testanalyzer;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.testanalyzer.producing.ProducingClass2;
import com.oneandone.cdi.testanalyzer.test2.Test2Interface;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Set;

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
        Configuration configuration = createTest(TestClass.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
//        Assert.assertNotNull(test2b);
//        assertEquals("Test2B", test2b.call());
    }
}
