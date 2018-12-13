package com.oneandone.cdi.weld;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.StarterDeploymentException;

/**
 * @author aschoerk
 */
public class InnerClassTest extends WeldStarterTestsBase {
    public static class Dummy {

    }

    public static class DummyInner {

    }

    public static class DummyInnerInner {

    }

    public static class Bean {
        public static class InnerBean {
            public static class InnerInnerBean {
                @Inject
                DummyInnerInner dummyInnerInner;
            }

            @Inject
            DummyInner dummyInner;
        }

        @Inject
        Dummy dummy;
    }

    @Test
    public void testInner() {
        setBeanClasses(Bean.InnerBean.class, DummyInner.class);
        start();
        assertNotNull(selectGet(Bean.InnerBean.class));
        assertNotNull(selectGet(DummyInner.class));

    }

    @Test
    public void testInnerInner() {
        setBeanClasses(Bean.InnerBean.InnerInnerBean.class, DummyInnerInner.class);
        start();
        assertNotNull(selectGet(Bean.InnerBean.InnerInnerBean.class));
        assertNotNull(selectGet(DummyInnerInner.class));

    }

    @Test(expected = StarterDeploymentException.class)
    public void testInnerInnerUnfilled() {
        setBeanClasses(Bean.InnerBean.InnerInnerBean.class);
        start();
    }
}
