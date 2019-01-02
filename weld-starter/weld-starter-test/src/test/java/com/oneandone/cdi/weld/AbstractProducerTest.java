package com.oneandone.cdi.weld;

import org.junit.Test;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

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


    static abstract class AbstractClassToBeInjected {

            static class ToInclude {
                public static int count;
                @PostConstruct
                public void postConstruct() {
                    count++;
                }
            }

            @Inject
            ToInclude toInclude;
            @Produces
            ToInclude tmp = new ToInclude(); // no produces clash with excluded ToExclude

    }


    @ApplicationScoped
    static class ConcreteClass3 extends AbstractClassToBeInjected {

    }


    @Test
    public void test3() {
        setBeanClasses(ConcreteClass3.class);
        start();
    }
}
