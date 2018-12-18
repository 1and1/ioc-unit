package com.oneandone.cdi.weld;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;



/**
 * @author aschoerk
 */
public class ProviderAndInstanceTest extends WeldStarterTestsBase {
    public interface I {
        boolean call();
    }

    public static class IImpl implements I {
        public boolean call() {
            return true;
        }
    }

    public static class IImpl2 implements I {
        public boolean call() {
            return false;
        }
    }

    public static class BeanWithProvider {
        @Inject
        Provider<I> iProvided;
    }

    public static class BeanWithInstance {
        @Inject
        Instance<I> iInstance;
    }


    @Test
    public void testNormal() {
        setBeanClasses(BeanWithProvider.class, IImpl2.class);
        start();
        assertNotNull(selectGet(BeanWithProvider.class).iProvided);
        assertNotNull(selectGet(BeanWithProvider.class).iProvided.get());
        assertFalse(selectGet(BeanWithProvider.class).iProvided.get().call());
    }

    @Test
    public void providerUsableWithoutBean() {
        setBeanClasses(BeanWithProvider.class);
        start();
        assertNotNull(selectGet(BeanWithProvider.class).iProvided);
    }

    @Test(expected = UnsatisfiedResolutionException.class)
    public void providerNeedsBean() {
        setBeanClasses(BeanWithProvider.class);
        start();
        assertNotNull(selectGet(BeanWithProvider.class).iProvided);
        assertNotNull(selectGet(BeanWithProvider.class).iProvided.get());
    }

    @Test
    public void instanceNeedsNoBean() {
        setBeanClasses(BeanWithInstance.class);
        start();
        assertNotNull(selectGet(BeanWithInstance.class).iInstance);
        assertTrue(selectGet(BeanWithInstance.class).iInstance.isUnsatisfied());
    }


    @Test
    public void instanceGetsBean() {
        setBeanClasses(BeanWithInstance.class, IImpl.class);
        start();
        assertNotNull(selectGet(BeanWithInstance.class).iInstance);
        assertFalse(selectGet(BeanWithInstance.class).iInstance.isUnsatisfied());
        assertFalse(selectGet(BeanWithInstance.class).iInstance.isAmbiguous());
        assertTrue(selectGet(BeanWithInstance.class).iInstance.select(I.class).get().call());
        assertTrue(selectGet(BeanWithInstance.class).iInstance.select(IImpl.class).get().call());
    }

    @Test(expected = UnsatisfiedResolutionException.class)
    public void instanceGetsOnlyRightBean() {
        setBeanClasses(BeanWithInstance.class, IImpl.class);
        start();
        assertTrue(selectGet(BeanWithInstance.class).iInstance.select(IImpl2.class).get().call());
    }

    @Test
    public void instanceGetsSuperClass() {
        setBeanClasses(BeanWithInstance.class, IImpl.class);
        start();
        assertTrue(selectGet(BeanWithInstance.class).iInstance.select(I.class).get().call());
    }

    @Test
    public void instanceGets2Beans() {
        setBeanClasses(BeanWithInstance.class, IImpl.class, IImpl2.class);
        start();
        assertNotNull(selectGet(BeanWithInstance.class).iInstance);
        assertFalse(selectGet(BeanWithInstance.class).iInstance.isUnsatisfied());
        assertTrue(selectGet(BeanWithInstance.class).iInstance.isAmbiguous());

        assertTrue(selectGet(BeanWithInstance.class).iInstance.select(IImpl.class).get().call());
        assertFalse(selectGet(BeanWithInstance.class).iInstance.select(IImpl2.class).get().call());
    }

    @Test(expected = AmbiguousResolutionException.class)
    public void instanceDoesNotAllowAmbiguousAccess() {
        setBeanClasses(BeanWithInstance.class, IImpl.class, IImpl2.class);
        start();
        selectGet(BeanWithInstance.class).iInstance.select(I.class).get().call();
    }
}
