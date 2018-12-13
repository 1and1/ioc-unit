package com.oneandone.cdi.weld1starter;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class Weld1StarterTest extends WeldStarterTestBase {

    @Before
    public void init() {
        setWeldStarter(new WeldStarterImpl());
    }

    static class DummyBean {

    }

    static class TestBean {
        @Inject
        DummyBean dummyBean;
    }

    @Test
    public void test1() {
        setBeanClasses(TestBean.class, DummyBean.class);
        start();
        assertNotNull(selectGet(TestBean.class));
    }

    @Test
    public void testStartMain() {
        StartMain.main(new String[] {});
    }
}
