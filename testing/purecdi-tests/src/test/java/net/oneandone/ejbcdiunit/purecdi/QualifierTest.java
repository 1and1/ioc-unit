package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class QualifierTest extends WeldStarterTestBase {

    @Test
    public void testNoQualifier() {
        setBeanClasses(Bean2.class, ToInject.class);
        start();
        Bean2 bean2 = selectGet(Bean2.class);
        assertEquals(0, bean2.toInject.signifyClass());
        assertEquals(0, bean2.toInject_2.signifyClass());
    }

    @Test
    public void testAny() {
        setBeanClasses(Bean3.class, ToInject.class);
        start();
        Bean3 bean3 = selectGet(Bean3.class);
        assertEquals(0, bean3.toInject_2.signifyClass());
    }


    interface ToInjectIntf {
        int signifyClass();
    }


    static class ToInject implements ToInjectIntf {

        @Override
        public int signifyClass() {
            return 0;
        }
    }

    @Q1
    static class ToInjectQ1 implements ToInjectIntf {

        @Override
        public int signifyClass() {
            return 1;
        }
    }

    @Q2
    static class ToInjectQ2 implements ToInjectIntf {

        @Override
        public int signifyClass() {
            return 2;
        }
    }

    static class Bean {

        @Inject
        ToInject toInject;

        @Any
        @Inject
        ToInjectQ1 toInjectQ1;

        @Q1
        @Inject
        ToInjectIntf toInjectQ1_2;

        @Q2
        @Inject
        ToInjectIntf toInjectQ2;

    }

    static class Bean2 {
        @Inject
        ToInjectIntf toInject;

        @Any
        @Inject
        ToInjectIntf toInject_2;
    }

    static class Bean3 {

        @Any
        @Inject
        ToInjectIntf toInject_2;
    }

    @Test
    public void testAnyQ1() {
        setBeanClasses(Bean3.class, ToInjectQ1.class);
        start();
        Bean3 bean3 = selectGet(Bean3.class);
        assertEquals(1, bean3.toInject_2.signifyClass());
    }

    @Test
    public void testAnyQ2() {
        setBeanClasses(Bean3.class, ToInjectQ2.class);
        start();
        Bean3 bean3 = selectGet(Bean3.class);
        assertEquals(2, bean3.toInject_2.signifyClass());
    }

    @Test
    public void testInstanceSingleProvided() {
        setBeanClasses(Bean4.class, ToInjectQ2.class);
        start();
        Bean4 bean4 = selectGet(Bean4.class);
        assertEquals(2, bean4.toInjectIstances.select(ToInjectQ2.class).get().signifyClass());
    }

    @Test
    public void testInstanceAllProvided() {
        setBeanClasses(Bean4.class, ToInjectQ2.class, ToInjectQ1.class, ToInject.class);
        start();
        Bean4 bean4 = selectGet(Bean4.class);
        assertEquals(2, bean4.toInjectIstances.select(ToInjectQ2.class).get().signifyClass());
        assertEquals(1, bean4.toInjectIstances.select(ToInjectQ1.class).get().signifyClass());
        assertEquals(0, bean4.toInjectIstances.select(ToInject.class).get().signifyClass());
    }

    static class Bean4 {
        @Any
        @Inject
        Instance<ToInjectIntf> toInjectIstances;
    }

    @Test
    public void testProducers() {
        setBeanClasses(Bean5.class, Producing.class, ToInjectQ2.class, ToInject.class);
        start();
        Bean5 bean5 = selectGet(Bean5.class);
        assertEquals(2, bean5.toInjectQ1.signifyClass());
    }

    @Test(expected = StarterDeploymentException.class)
    public void testProducersAmbiguus() {
        setBeanClasses(Bean5.class, Producing.class, ToInjectQ2.class, ToInjectQ1.class, ToInject.class);
        start();
        Bean5 bean5 = selectGet(Bean5.class);
        assertEquals(2, bean5.toInjectQ1.signifyClass());
    }

    static class Bean5 {

        @Q1
        @Inject
        ToInjectIntf toInjectQ1;
    }

    @Test
    public void testQ1() {
        setBeanClasses(Bean.class, ToInject.class, ToInjectQ1.class, ToInjectQ2.class);
        start();
        Bean bean = selectGet(Bean.class);
        assertEquals(0, bean.toInject.signifyClass());
        assertEquals(1, bean.toInjectQ1.signifyClass());
        assertEquals(1, bean.toInjectQ1_2.signifyClass());
        assertEquals(2, bean.toInjectQ2.signifyClass());
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
    public @interface Q1 {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
    public @interface Q2 {

    }

    static class Producing {
        @Q1
        @Produces
        ToInjectIntf q1(@Q2 ToInjectIntf q) {
            return q;
        }

        ;
    }


}
