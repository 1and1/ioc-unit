package com.oneandone.cdi.weld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.oneandone.cdi.weld.beans.ConcreteClass3;
import com.oneandone.cdi.weld.beans.ToInclude;

public class AbstractProducerTest extends WeldStarterTestsBase {

    static abstract class AbstractClass {

        @Produces
        int producedInt() { return 10; }

        @Produces
        static int intProducer() { return 10; }
    }

    static class ConcreteClass extends AbstractClass {
        @Produces
        int producedInt() { return super.producedInt(); }
    }

    static class ConcreteClass2 extends AbstractClass {
        @Produces
        static int intProducer() {
            return 11;
        }
    }
    static class InjectingClass {
        @Inject
        int toBeInjected;
    }


    @Test
    public void test() {
        setBeanClasses(ConcreteClass.class, InjectingClass.class);
        start();
    }

    @Test
    public void test2() {
        setBeanClasses(ConcreteClass2.class, InjectingClass.class);
        start();
    }

    static class ToBeInjected {
    }


    @Ignore
    @Test
    public void test3() {
        setBeanClasses(ConcreteClass3.class);
        setExtensionObjects(Arrays.asList(new TestScopeExtension(ConcreteClass3.class)));
        start();
        ConcreteClass3 testBean = selectGet(ConcreteClass3.class);
        testBean.getTmp();
        assertEquals(1, ToInclude.count);
    }
}
