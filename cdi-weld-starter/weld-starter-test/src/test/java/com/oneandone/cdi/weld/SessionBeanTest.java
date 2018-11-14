package com.oneandone.cdi.weld;

import static org.junit.Assert.assertNotNull;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class SessionBeanTest extends WeldStarterTestBase {

    @Stateless
    public static class StatelessBean {

    }

    public static class Bean {
        @Inject
        StatelessBean statelessBean;
    }

    @Test
    public void test() {
        this.setBeanClasses(Bean.class, StatelessBean.class);
        start();
        assertNotNull(selectGet(Bean.class).statelessBean);
    }
}
