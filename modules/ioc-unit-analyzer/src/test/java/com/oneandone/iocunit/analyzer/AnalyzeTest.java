package com.oneandone.iocunit.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.MalformedURLException;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

/**
 * @author aschoerk
 */

public class AnalyzeTest extends BaseTest {

    @Test
    public void doesStartTestClass() {
        createTest(DoesStartTestClass.class);
        assertEquals(1, toBeStarted.size());
        assertEquals(DoesStartTestClass.class, toBeStarted.iterator().next());
    }

    @Test
    public void canReplaceSutClassByTestClass() throws MalformedURLException {
        createTest(TestClassOverridingInject.class);
        assertEquals(3, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClassOverridingInject.class));
        assertTrue(toBeStarted.contains(SutClass.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInject.TestSut2Class.class));
        assertFalse(toBeStarted.contains(SutClass.Sut2.class));
    }

    @Test
    public void canFindAvailableSutClass() throws MalformedURLException {
        createTest(TestClassNotOverridingInject.class);
        assertEquals(3, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClassNotOverridingInject.class));
        assertTrue(toBeStarted.contains(SutClass.class));
        assertTrue(toBeStarted.contains(SutClass.Sut2.class));
    }

    @Test
    public void availableTestExtendHasPrio() {
        createTest(TestClassOverridingInjectByAvailable.class);
        assertEquals(3, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerInnerSutTestClass.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerSutClass.class));
        assertFalse(toBeStarted.contains(TestClassOverridingInjectByAvailable.InnerSutClass.InnerInnerSutClass.class));

    }

    @Test
    public void availableTestInterfaceImplHasPrio() {
        createTest(TestClassOverridingInjectByAvailable2.class);
        assertEquals(3, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerInnerSutTestClass.class));
        assertTrue(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerSutClass.class));
        assertFalse(toBeStarted.contains(TestClassOverridingInjectByAvailable2.InnerSutClass.InnerInnerSutClass.class));

    }


    static class DoesStartTestClass extends BaseClass {

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
    static class TestClassOverridingInject extends BaseClass {
        static class TestSut2Class extends SutClass.Sut2 {

        }
    }

    @SutClasses(SutClass.class)
    static class TestClassNotOverridingInject extends BaseClass {
    }

    @SutClasses({TestClassOverridingInjectByAvailable.InnerSutClass.class})
    static class TestClassOverridingInjectByAvailable extends BaseClass {
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
    static class TestClassOverridingInjectByAvailable2 extends BaseClass {
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

    static interface SutClass2Intf {

    }

    static class SutClass21 implements  SutClass2Intf {

    }

    static class SutClass22 implements  SutClass2Intf {

    }

    @SutClasses(SutClass2Intf.class)
    @SutPackages(SutClass21.class)
    static class TestClass2 extends BaseClass {

    }

    @Test
    public void doesStartAvailableAssignablesClass() {
        createTest(TestClass2.class);
        assertEquals(4, toBeStarted.size());
        assertTrue(toBeStarted.contains(SutClass21.class));
        assertTrue(toBeStarted.contains(SutClass22.class));
        assertTrue(toBeStarted.contains(SutClass2Intf.class));
        assertTrue(toBeStarted.contains(TestClass2.class));
    }

    @SutPackages(SutClass21.class)
    static class TestClass2NoIntf extends BaseClass {

    }

    @Test
    public void doesStartOnlyAvailableAssignablesClass() {
        createTest(TestClass2NoIntf.class);
        assertEquals(1, toBeStarted.size());
        assertTrue(toBeStarted.contains(TestClass2NoIntf.class));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Annotation {

    }

    @Annotation
    static class SutClass31 {

    }

    @Annotation
    static class SutClass32 {

    }

    @SutClasses(Annotation.class)
    @SutPackages(SutClass31.class)
    static class TestClass3 extends BaseClass {

    }

    @Test
    public void doesStartAvailableAnnotatedClass() {
        createTest(TestClass3.class);
        assertEquals(4, toBeStarted.size());
        assertTrue(toBeStarted.contains(SutClass31.class));
        assertTrue(toBeStarted.contains(SutClass32.class));
        assertTrue(toBeStarted.contains(Annotation.class));
        assertTrue(toBeStarted.contains(TestClass3.class));
    }

    static abstract class SutClass4Abstract {

    }

    static class SutClass41 extends SutClass4Abstract {

    }

    static class SutClass42 extends SutClass4Abstract {

    }

    @SutClasses(SutClass4Abstract.class)
    @SutPackages(SutClass41.class)
    static class TestClass4 extends BaseClass {

    }

    @Test
    public void doesStartAvailableExtendingClass() {
        createTest(TestClass4.class);
        assertEquals(4, toBeStarted.size());
        assertTrue(toBeStarted.contains(SutClass41.class));
        assertTrue(toBeStarted.contains(SutClass42.class));
        assertTrue(toBeStarted.contains(SutClass4Abstract.class));
        assertTrue(toBeStarted.contains(TestClass4.class));
    }


}

