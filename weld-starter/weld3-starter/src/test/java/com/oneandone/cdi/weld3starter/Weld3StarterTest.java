package com.oneandone.cdi.weld3starter;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class Weld3StarterTest extends WeldStarterTestBase {

    static class TestBean {

    }

    @Test
    public void test1() {
        setBeanClasses(TestBean.class);
        start();
        assertNotNull(selectGet(TestBean.class));
    }
}
