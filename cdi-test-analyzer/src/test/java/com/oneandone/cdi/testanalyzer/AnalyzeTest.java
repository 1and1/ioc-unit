package com.oneandone.cdi.testanalyzer;

import java.net.MalformedURLException;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class AnalyzeTest {

    Configuration createTest(Class<?> clazz) {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .testClass(clazz);

        ConfigCreator cdiConfigCreator = new ConfigCreator();
        cdiConfigCreator.create(initialConfiguration);
        return cdiConfigCreator.getConfiguration();

    }

    static class DoesStartTestClass {

    }

    @Test
    public void doesStartTestClass() {
        Configuration configuration = createTest(DoesStartTestClass.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        Assert.assertEquals(1, toBeStarted.size());
        Assert.assertEquals(DoesStartTestClass.class, toBeStarted.iterator().next());
    }

    @Test
    public void canReplaceSutClassByTestClass() throws MalformedURLException {
        Configuration configuration = createTest(TestClassOverridingInject.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        Assert.assertEquals(3, toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(TestClassOverridingInject.class));
        Assert.assertTrue(toBeStarted.contains(SutClass.class));
        Assert.assertTrue(toBeStarted.contains(TestClassOverridingInject.TestSut2Class.class));
        Assert.assertFalse(toBeStarted.contains(SutClass.Sut2.class));
    }

    @Test
    public void canFindAvailableSutClass() throws MalformedURLException {
        Configuration configuration = createTest(TestClassNotOverridingInject.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        Assert.assertEquals(3, toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(TestClassNotOverridingInject.class));
        Assert.assertTrue(toBeStarted.contains(SutClass.class));
        Assert.assertTrue(toBeStarted.contains(SutClass.Sut2.class));
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

    @Test
    public void availableTestExtendHasPrio() {
        Configuration configuration = createTest(TestClassOverridingInjectByAvailable.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        Assert.assertEquals(3, toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.class));
        Assert.assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerInnerSutTestClass.class));
        Assert.assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerSutClass.class));
        Assert.assertFalse(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerSutClass.InnerInnerSutClass.class));

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


    @Test
    public void availableTestInterfaceImplHasPrio() {
        Configuration configuration = createTest(TestClassOverridingInjectByAvailable2.class);
        final Set<Class<?>> toBeStarted = configuration.getToBeStarted();
        Assert.assertEquals(3, toBeStarted.size());
        Assert.assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.class));
        Assert.assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerInnerSutTestClass.class));
        Assert.assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerSutClass.class));
        Assert.assertFalse(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerSutClass.InnerInnerSutClass.class));

    }


}

