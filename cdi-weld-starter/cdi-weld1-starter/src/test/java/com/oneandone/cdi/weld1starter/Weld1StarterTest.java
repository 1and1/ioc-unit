package com.oneandone.cdi.weld1starter;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

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
