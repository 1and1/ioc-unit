package com.oneandone.cdi.weldstarter;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.oneandone.cdi.weld.WeldStarterTestsBase;

/**
 * @author aschoerk
 */
public class TestWeldStarter extends WeldStarterTestsBase {

    static class TestBean {

    }

    @Test
    public void test1() {
        setBeanClasses(TestBean.class);
        start();
        assertNotNull(selectGet(TestBean.class));
    }


}
