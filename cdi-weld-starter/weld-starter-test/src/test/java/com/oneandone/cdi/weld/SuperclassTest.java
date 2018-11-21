package com.oneandone.cdi.weld;

import static org.junit.Assert.assertNotNull;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Test;



/**
 * @author aschoerk
 */
public class SuperclassTest extends WeldStarterTestsBase {

    public static class ToInjectInSubClass {}

    public static class ToInjectInSuperClass {}

    private static class ProducedInSubClass {}

    private static class ProducedInSuperClass {}

    public static class SuperClass {
        @Inject
        ToInjectInSuperClass toInjectInSuperClass;
        @Produces
        public ProducedInSuperClass producedInSuperClass = new ProducedInSuperClass();
    }

    public static class SubClass extends SuperClass {
        @Inject
        ToInjectInSubClass toInjectInSubClass;

        @Produces
        ProducedInSubClass producedInSubClass = new ProducedInSubClass();

        @Produces
        ProducedInSuperClass producedInSuperClassInSubClass = producedInSuperClass;
    }

    public static class BeanWithSuperClass {
        @Inject
        SuperClass superClass;
        @Inject
        ProducedInSuperClass producedInSuperClass;
    }

    public static class BeanWithSubClass {
        @Inject
        SuperClass superClass;
        @Inject
        SubClass subClass;
        @Inject
        ProducedInSuperClass producedInSuperClass;
        @Inject
        ProducedInSubClass producedInSubClass;
    }

    public static class SuperClassWithInjectConstructor {
        int i = 1;
        ToInjectInSuperClass toInjectInSuperClass;

        @Inject
        public SuperClassWithInjectConstructor(ToInjectInSuperClass toInjectInSuperClass) {
            this.toInjectInSuperClass = toInjectInSuperClass;
        }
    }


    public static class SubClassWithInjectConstructor extends SuperClassWithInjectConstructor {

        @Inject
        public SubClassWithInjectConstructor(final ToInjectInSuperClass toInjectInSuperClass) {
            super(toInjectInSuperClass);
            super.i = 2;
        }
    }

    public static class SubClassWithoutInjectConstructor extends SuperClassWithInjectConstructor {

        @Inject
        public SubClassWithoutInjectConstructor() {
            super(null);
            super.i = 3;
        }
    }

    @Test
    public void testSubClassWithoutInjectConstructor() {
        this.setBeanClasses(SubClassWithoutInjectConstructor.class);
        start();
        assertEquals(3, selectGet(SubClassWithoutInjectConstructor.class).i);
    }

    @Test
    public void testSubClassWithInjectConstructor() {
        this.setBeanClasses(SubClassWithInjectConstructor.class,
                ToInjectInSuperClass.class);
        start();
        assertEquals(2, selectGet(SubClassWithInjectConstructor.class).i);
    }

    @Test
    public void testSuperClassWithInjectConstructor() {
        this.setBeanClasses(SuperClassWithInjectConstructor.class,
                ToInjectInSuperClass.class);
        start();
        assertEquals(1, selectGet(SuperClassWithInjectConstructor.class).i);
    }

    @Test
    public void testSuperClass() {
        this.setBeanClasses(BeanWithSuperClass.class,
                SuperClass.class, ToInjectInSuperClass.class);
        start();
        assertNotNull(selectGet(BeanWithSuperClass.class).producedInSuperClass);
        assertNotNull(selectGet(BeanWithSuperClass.class).superClass);
        assertNotNull(selectGet(BeanWithSuperClass.class).superClass.toInjectInSuperClass);
    }


    // Producers are not inherited !!!!
    @Test
    public void testSuperClassWithSub() {
        this.setBeanClasses(
                BeanWithSubClass.class,
                SubClass.class,
                ToInjectInSuperClass.class, ToInjectInSubClass.class);
        start();
        assertNotNull(selectGet(BeanWithSubClass.class).producedInSuperClass);
        assertNotNull(selectGet(BeanWithSubClass.class).superClass);
        assertNotNull(selectGet(BeanWithSubClass.class).superClass.toInjectInSuperClass);
        assertNotNull(selectGet(BeanWithSubClass.class).superClass.producedInSuperClass);
        assertNotNull(selectGet(BeanWithSubClass.class).subClass);
        assertNotNull(selectGet(BeanWithSubClass.class).subClass.toInjectInSuperClass);
        assertNotNull(selectGet(BeanWithSubClass.class).subClass.toInjectInSubClass);
        assertNotNull(selectGet(BeanWithSubClass.class).subClass.producedInSuperClassInSubClass);
    }


    public static class SuperClassWithInjectInit {
        int i = 0;
        ToInjectInSuperClass toInjectInSuperClass;

        @Inject
        public void setToInjectInSuperClass(ToInjectInSuperClass toInjectInSuperClass) {
            this.toInjectInSuperClass = toInjectInSuperClass;
            i = 2;
        }
    }

    public static class SubClassWithInjectInit extends SuperClassWithInjectInit {}


    public static class SubClassWithInjectInitOverriden extends SuperClassWithInjectInit {

        public SubClassWithInjectInitOverriden() {
            super.i = 3;
        }

        @Override
        public void setToInjectInSuperClass(ToInjectInSuperClass toInjectInSuperClassP) {
            this.toInjectInSuperClass = null;
        }
    }


    @Test
    public void testSuperClassWithInjectInit() {
        setBeanClasses(SubClassWithInjectInit.class, ToInjectInSuperClass.class);
        start();
        assertNotNull(selectGet(SubClassWithInjectInit.class).toInjectInSuperClass);
        assertEquals(2, selectGet(SubClassWithInjectInit.class).i);
    }

    @Test
    public void testSubClassWithInjectInitOverriden() {
        setBeanClasses(SubClassWithInjectInitOverriden.class, ToInjectInSuperClass.class);
        start();
        assertNull(selectGet(SubClassWithInjectInitOverriden.class).toInjectInSuperClass);
        assertEquals(3, selectGet(SubClassWithInjectInitOverriden.class).i);
    }


}
