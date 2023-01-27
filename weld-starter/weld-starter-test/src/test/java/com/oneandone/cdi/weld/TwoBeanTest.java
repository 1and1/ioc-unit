package com.oneandone.cdi.weld;

import static org.junit.Assert.assertTrue;

import jakarta.inject.Inject;

import org.junit.Test;



/**
 * @author aschoerk
 */
public class TwoBeanTest extends WeldStarterTestsBase {


    static class CdiHelperBean {
        public boolean callHelper() {
            return true;
        }
    }

    static class CdiBean1 {
        @Inject
        CdiHelperBean cdiHelperBean;

        public boolean callThis() {
            return true;
        }

        public CdiHelperBean getCdiHelperBean() {
            return cdiHelperBean;
        }
    }


    @Test
    public void test() {
        setBeanClasses(CdiBean1.class, CdiHelperBean.class);
        start();

        assertTrue(selectGet(CdiBean1.class).callThis());
        assertTrue(selectGet(CdiBean1.class).getCdiHelperBean().callHelper());
    }


}
