package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertNotNull;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class SuperclassTest extends WeldStarterTestBase {

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

}
