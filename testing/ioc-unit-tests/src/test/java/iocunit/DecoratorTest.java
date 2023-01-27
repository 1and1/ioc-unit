package iocunit;

import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.IocUnitRunner;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ DecoratorTest.Bean.class, DecoratorTest.IImpl.class, DecoratorTest.IDecorator1.class })
public class DecoratorTest {
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

    @Inject
    Bean b;

    @Inject
    IImpl iimpl;

    @Inject
    BeanManager beanManager;

    @Test
    public void canDecorate() {
        Assert.assertNotNull(b);
        Assert.assertNotNull(iimpl);
        Assert.assertNotEquals("Impl", iimpl.call());
        // Assert.assertNotNull(selectGet(IDecorator1.class));
        Assert.assertEquals("Impl_Decorator1", b.injected.call());

    }

}
