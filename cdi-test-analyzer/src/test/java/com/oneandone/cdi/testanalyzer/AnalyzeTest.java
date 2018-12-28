package com.oneandone.cdi.testanalyzer;

import com.oneandone.cdi.testanalyzer.annotations.EnabledAlternatives;
import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author aschoerk
 */

public class AnalyzeTest extends BaseTest {

    @Test
    public void doesStartTestClass() {
        Configuration configuration = createTest(DoesStartTestClass.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        assertEquals(1, toBeStarted.size());
        assertEquals(DoesStartTestClass.class, toBeStarted.iterator().next());
    }

    @Test
    public void canReplaceSutClassByTestClass() throws MalformedURLException {
        Configuration configuration = createTest(TestClassOverridingInject.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        assertEquals(3, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClassOverridingInject.class));
        assertTrue(toBeStarted.contains(SutClass.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInject.TestSut2Class.class));
        assertFalse(toBeStarted.contains(SutClass.Sut2.class));
    }

    @Test
    public void canFindAvailableSutClass() throws MalformedURLException {
        Configuration configuration = createTest(TestClassNotOverridingInject.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        assertEquals(3, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClassNotOverridingInject.class));
        assertTrue(toBeStarted.contains(SutClass.class));
        assertTrue(toBeStarted.contains(SutClass.Sut2.class));
    }

    @Test
    public void availableTestExtendHasPrio() {
        Configuration configuration = createTest(TestClassOverridingInjectByAvailable.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        assertEquals(3, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerInnerSutTestClass.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerSutClass.class));
        assertFalse(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerSutClass.InnerInnerSutClass.class));

    }

    @Test
    public void availableTestInterfaceImplHasPrio() {
        Configuration configuration = createTest(TestClassOverridingInjectByAvailable2.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        assertEquals(3, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerInnerSutTestClass.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerSutClass.class));
        assertFalse(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerSutClass.InnerInnerSutClass.class));

    }



    static class DoesStartTestClass {

    }

    static class SutClass {
        @Inject
        Sut2 sut2;

        static class Sut1 {

        }

        static class Sut2 {

        }

    }

    @SutClasses(SutClass.class)
    @TestClasses(TestClassOverridingInject.TestSut2Class.class)
    static class TestClassOverridingInject {
        static class TestSut2Class extends SutClass.Sut2 {

        }
    }

    @SutClasses(SutClass.class)
    static class TestClassNotOverridingInject {
    }

    @SutClasses({TestClassOverridingInjectByAvailable.InnerSutClass.class})
    static class TestClassOverridingInjectByAvailable {
        static class InnerSutClass {

            @Inject
            InnerInnerSutClass innerInnerSutClass;

            // Inject should be overridden by InnerTestSutClass!! because, this is Sut
            static class InnerInnerSutClass {

            }
        }

        static class InnerInnerSutTestClass extends InnerSutClass.InnerInnerSutClass {

        }
    }

    @SutClasses({TestClassOverridingInjectByAvailable2.InnerSutClass.class})
    static class TestClassOverridingInjectByAvailable2 {
        static class InnerSutClass {

            @Inject
            InnerInnerSutInterface innerInnerSutClass;

            interface InnerInnerSutInterface {

            }

            // Inject should be overridden by InnerTestSutClass!! because, this is Sut
            static class InnerInnerSutClass {

            }
        }

        static class InnerInnerSutTestClass implements InnerSutClass.InnerInnerSutInterface {

        }
    }


}

