package com.oneandone.cdi.weld;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.WrappedDeploymentException;

/**
 * @author aschoerk
 */
public class QualifierTest extends WeldStarterTestsBase {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface Q1 {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface Q2 {

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

    static class Bean4 {
        @Any
        @Inject
        Instance<ToInjectIntf> toInjectIstances;
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

    static class Bean5 {

        @Q1
        @Inject
        ToInjectIntf toInjectQ1;
    }


    static class ProducingQ1_InjectQ2 {
        @Q1
        @Produces
        ToInjectIntf q1(@Q2 ToInjectIntf q) {
            return q;
        };
    }

    @Test
    public void testProducers() {
        setBeanClasses(Bean5.class, ProducingQ1_InjectQ2.class, ToInjectQ2.class, ToInject.class);
        start();
        Bean5 bean5 = selectGet(Bean5.class);
        assertEquals(2, bean5.toInjectQ1.signifyClass());
    }

    @Test(expected = WrappedDeploymentException.class)
    public void testProducersAmbiguus() {
        setBeanClasses(Bean5.class, ProducingQ1_InjectQ2.class, ToInjectQ2.class, ToInjectQ1.class, ToInject.class);
        start();
        Bean5 bean5 = selectGet(Bean5.class);
        assertEquals(2, bean5.toInjectQ1.signifyClass());
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

    static class Bean6 {

        @Q1
        @Q2
        @Inject
        ToInjectIntf toInjectQ1Q2;
    }


    static class ProducingQ1Q2 {
        @Q1
        @Q2
        @Produces
        ToInjectIntf q1() {
            return new ToInjectIntf() {
                @Override
                public int signifyClass() {
                    return 20;
                }
            };
        };
    }

    @Test
    public void testQ1Q2() {
        setBeanClasses(Bean6.class, ProducingQ1Q2.class);
        start();
        Bean6 bean = selectGet(Bean6.class);
        assertEquals(20, bean.toInjectQ1Q2.signifyClass());
    }

    static class Producing_Q2 {
        @Q2
        @Produces
        ToInjectIntf q1() {
            return new ToInjectIntf() {
                @Override
                public int signifyClass() {
                    return 20;
                }
            };
        };
    }

    @Test(expected = WrappedDeploymentException.class)
    public void testQ1Q2_OnlyQ2Produced() {
        setBeanClasses(Bean6.class, Producing_Q2.class);
        start();
    }

    static class Bean6_Q1 {

        @Q1
        @Inject
        ToInjectIntf toInjectQ1Q2;
    }

    @Test
    public void testQ1Q2_intoQ1() {
        setBeanClasses(Bean6_Q1.class, ProducingQ1Q2.class);
        start();
        Bean6_Q1 bean = selectGet(Bean6_Q1.class);
        assertEquals(20, bean.toInjectQ1Q2.signifyClass());
    }

    static class Bean6_Q2 {
        @Q1
        @Inject
        ToInjectIntf toInjectQ1Q2;
    }

    @Test
    public void testQ1Q2_intoQ2() {
        setBeanClasses(Bean6_Q2.class, ProducingQ1Q2.class);
        start();
        Bean6_Q2 bean = selectGet(Bean6_Q2.class);
        assertEquals(20, bean.toInjectQ1Q2.signifyClass());
    }

    static class Bean7_Named {

        @Inject
        ToInjectIntf toInjectNamed;
    }

    static class Producing6_Named {
        @Named
        @Produces
        ToInjectIntf q1() {
            return new ToInjectIntf() {
                @Override
                public int signifyClass() {
                    return 21;
                }
            };
        };
    }

    @Test
    public void testNamed_intoDefault() {
        setBeanClasses(Bean7_Named.class, Producing6_Named.class);
        start();
        Bean7_Named bean = selectGet(Bean7_Named.class);
        assertEquals(21, bean.toInjectNamed.signifyClass());
    }

    static class Bean7_Namedq1 {
        @Named
        @Inject
        ToInjectIntf q1;
    }

    @Test
    public void testNamed_intoNamed() {
        setBeanClasses(Bean7_Namedq1.class, Producing6_Named.class);
        start();
        Bean7_Namedq1 bean = selectGet(Bean7_Namedq1.class);
        assertEquals(21, bean.q1.signifyClass());
    }

    static class Bean7_Namedq1_2 {

        @Named("q1")
        @Inject
        ToInjectIntf toInjectNamed;
    }


    @Test
    public void testNamed_intoNamedq1_2() {
        setBeanClasses(Bean7_Namedq1_2.class, Producing6_Named.class);
        start();
        Bean7_Namedq1_2 bean = selectGet(Bean7_Namedq1_2.class);
        assertEquals(21, bean.toInjectNamed.signifyClass());
    }

    static class Bean7_Namedq1_3 {

        @Named("q1")
        @Default
        @Inject
        ToInjectIntf toInjectNamed;
    }

    @Test
    public void testNamed_intoNamedq1_3() {
        setBeanClasses(Bean7_Namedq1_3.class, Producing6_Named.class);
        start();
        Bean7_Namedq1_3 bean = selectGet(Bean7_Namedq1_3.class);
        assertEquals(21, bean.toInjectNamed.signifyClass());
    }

    static class Producing_Any {
        @Any
        @Produces
        ToInjectIntf q1() {
            return new ToInjectIntf() {
                @Override
                public int signifyClass() {
                    return 22;
                }
            };
        };
    }

    @Test(expected = WrappedDeploymentException.class)
    public void testAny_intoQ1() {
        setBeanClasses(Bean6_Q1.class, Producing_Any.class);
        start();
    }


    static class BeanAny {
        @Any
        @Inject
        ToInjectIntf toInjectAny;
    }

    static class BeanEmpty {
        @Inject
        ToInjectIntf toInjectAny;
    }

    static class BeanDefault {
        @Default
        @Inject
        ToInjectIntf toInjectAny;
    }

    @Test
    public void testAny_intoAny() {
        setBeanClasses(BeanAny.class, Producing_Any.class);
        start();
        BeanAny bean = selectGet(BeanAny.class);
        assertEquals(22, bean.toInjectAny.signifyClass());
    }

    static class BeanAnyQ2 {
        @Any
        @Q2
        @Inject
        ToInjectIntf toInjectAny;
    }

    @Test(expected = WrappedDeploymentException.class)
    public void testAny_intoAnyQ1() {
        setBeanClasses(BeanAnyQ2.class, Producing_Any.class);
        start();
        BeanAnyQ2 bean = selectGet(BeanAnyQ2.class);
        assertEquals(22, bean.toInjectAny.signifyClass());
    }

    @Test
    public void testQ2_intoAnyQ2() {
        setBeanClasses(BeanAnyQ2.class, Producing_Q2.class);
        start();
        BeanAnyQ2 bean = selectGet(BeanAnyQ2.class);
        assertEquals(20, bean.toInjectAny.signifyClass());
    }

    @Test(expected = WrappedDeploymentException.class)
    public void testAny_intoEmpty() {
        setBeanClasses(BeanEmpty.class, Producing_Any.class);
        start();
        BeanEmpty bean = selectGet(BeanEmpty.class);
        assertEquals(22, bean.toInjectAny.signifyClass());
    }

    @Test(expected = WrappedDeploymentException.class)
    public void testAny_intoDefault() {
        setBeanClasses(BeanDefault.class, Producing_Any.class);
        start();
        BeanDefault bean = selectGet(BeanDefault.class);
        assertEquals(22, bean.toInjectAny.signifyClass());
    }

}
