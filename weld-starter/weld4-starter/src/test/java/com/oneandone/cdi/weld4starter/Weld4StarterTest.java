package com.oneandone.cdi.weld4starter;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class Weld4StarterTest extends WeldStarterTestBase {

    static class TestBean {

    }

    @Test
    public void test1() {
        setBeanClasses(TestBean.class);
        start();
        assertNotNull(selectGet(TestBean.class));
    }
}
