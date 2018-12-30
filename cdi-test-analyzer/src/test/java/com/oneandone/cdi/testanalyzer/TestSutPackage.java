package com.oneandone.cdi.testanalyzer;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.testanalyzer.test1.Test1A;
import com.oneandone.cdi.testanalyzer.test1.Test1Interface;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * @author aschoerk
 */


public class TestSutPackage extends BaseTest {

    @SutPackages({Test1A.class})
    static class TestClass {
        @Inject
        Test1A test1;

        @Inject
        Test1Interface test1Interface;
    }

    @Test
    public void test() {
        Configuration configuration = createTest(TestClass.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
//        assertNotNull(test1);
//        assertEquals("Test1A", test1.call());
    }

    @Test
    public void canOptimizeInjecting() {
        Configuration configuration = createTest(TestClass.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
//        assertNotNull(test1Interface);
//        assertEquals("Test1A", test1Interface.call());
    }
}
