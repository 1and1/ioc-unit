package com.oneandone.cdi.weld;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.StarterDeploymentException;

/**
 * @author aschoerk
 */
public class InitMethodTest extends WeldStarterTestsBase {

    static class DummyI {
        int i = 1;
    }

    public static class Bean {
        private int i;

        @Inject
        public void setI(DummyI dummyI) {
            this.i = dummyI.i;
        }
    }

    public static class BeanWithPrivateInitMethod {
        private int i;

        @Inject
        private void setI(DummyI dummyI) {
            this.i = dummyI.i;
        }
    }

    @Test
    public void testInitMethod() {
        setBeanClasses(Bean.class, DummyI.class);
        start();
        assertEquals(1, selectGet(Bean.class).i);
    }

    @Test
    public void testInitMethodPrivate() {
        setBeanClasses(BeanWithPrivateInitMethod.class, DummyI.class);
        start();
        assertEquals(1, selectGet(BeanWithPrivateInitMethod.class).i);
    }

    @Test(expected = StarterDeploymentException.class)
    public void testInitMethodNotFilledParam() {
        setBeanClasses(Bean.class);
        start();
    }

    @Test(expected = StarterDeploymentException.class)
    public void testInitMethodNotFilledParamInPrivate() {
        setBeanClasses(BeanWithPrivateInitMethod.class);
        start();
    }
}
