package com.oneandone.cdi.weld;

import static org.junit.Assert.assertNotNull;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class SessionBeanTest extends WeldStarterTestsBase {

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
