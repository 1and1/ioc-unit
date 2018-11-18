package com.oneandone.cdi.weld;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.weld.exceptions.DefinitionException;
import org.junit.Assert;
import org.junit.Test;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class DecoratorTest extends WeldStarterTestBase {

    interface I {
        String call();
    }

    static class IImpl implements I {
        public String call() {
            return "Impl";
        }
    }

    @Decorator
    static class IDecorator1 implements I {
        @Delegate
        @Inject
        I decorated;

        public String call() {
            return decorated.call() + "_Decorator1";
        }
    }

    static class Bean {

        @Inject
        I injected;
    }

    @Test
    public void canDecorate() {
        setBeanClasses(Bean.class, IImpl.class, IDecorator1.class);
        setDecoratorClasses(IDecorator1.class);
        start();
        Assert.assertNotNull(selectGet(Bean.class));
        Assert.assertNotNull(selectGet(IImpl.class));
        // Assert.assertNotNull(selectGet(IDecorator1.class));
        Assert.assertEquals("Impl_Decorator1", selectGet(Bean.class).injected.call());
        Assert.assertEquals("Impl_Decorator1", selectGet(IImpl.class).call());
        Assert.assertNotEquals("Impl", selectGet(IImpl.class).call());
    }

    @Decorator
    static class IDecorator2 implements I {
        @Delegate
        @Inject
        I decorated;

        public String call() {
            return decorated.call() + "_Decorator2";
        }

    }


    @Test
    public void canDecorate2Times() {
        setBeanClasses(Bean.class, IImpl.class, IDecorator1.class, IDecorator2.class);
        setDecoratorClasses(IDecorator1.class, IDecorator2.class);
        start();
        Assert.assertNotNull(selectGet(Bean.class));
        Assert.assertNotNull(selectGet(IImpl.class));
        // Assert.assertNotNull(selectGet(IDecorator1.class));
        Assert.assertEquals("Impl_Decorator2_Decorator1", selectGet(Bean.class).injected.call());
        Assert.assertEquals("Impl_Decorator2_Decorator1", selectGet(IImpl.class).call());
        Assert.assertNotEquals("Impl", selectGet(IImpl.class).call());
        tearDown();
        setDecoratorClasses(IDecorator2.class, IDecorator1.class);
        start();
        Assert.assertEquals("Impl_Decorator1_Decorator2", selectGet(IImpl.class).call());
    }

    static class DummyBean {
        String doSomething() {
            return "dummybean did something.";
        }

    }


    @Decorator
    static class IDecorator3 implements I {
        @Delegate
        @Inject
        I decorated;

        public String call() {
            return decorated.call() + "_Decorator3";
        }

        @Produces
        DummyBean dummyBean;
    }

    @Test(expected = DefinitionException.class)
    public void cannotProduceInDecorator() {
        setBeanClasses(Bean.class, IImpl.class, IDecorator3.class);
        setDecoratorClasses(IDecorator3.class);
        start();

    }

    @Decorator
    static class IDecorator4 implements I {
        @Delegate
        @Inject
        I decorated;

        public String call() {
            return decorated.call() + "_Decorator4 " + dummyBean.doSomething();
        }

        @Inject
        DummyBean dummyBean;
    }

    @Test
    public void canInjectIntoDecorator() {
        setBeanClasses(Bean.class, IImpl.class, IDecorator4.class, DummyBean.class);
        setDecoratorClasses(IDecorator4.class);
        start();
        Assert.assertEquals("Impl_Decorator4 dummybean did something.", selectGet(IImpl.class).call());
    }


}
