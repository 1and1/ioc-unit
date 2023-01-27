package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import jakarta.inject.Inject;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class TwoBeanWithGenericsTest extends com.oneandone.cdi.weldstarter.WeldStarterTestBase {

    static class CdiHelperBean<T> {
        T field = null;

        public boolean callHelper() {
            return true;
        }
    }

    static class CdiParamInjectHelperBean<T> {
        @Inject
        CdiHelperBean<T> field;

        public boolean callHelper() {
            return false;
        }

    }


    static class CdiBean1 {
        @Inject
        CdiHelperBean<Integer> cdiHelperBean;

        @Inject
        CdiParamInjectHelperBean<Integer> cdiHelperBeanCdiParamInjectHelperBean;

        public boolean callThis() {
            return true;
        }

        public CdiHelperBean getCdiHelperBean() {
            return cdiHelperBean;
        }

        public CdiParamInjectHelperBean<Integer> getCdiHelperBeanCdiParamInjectHelperBean() {
            return cdiHelperBeanCdiParamInjectHelperBean;
        }
    }


    @Test
    public void test() {
        setBeanClasses(CdiBean1.class, CdiHelperBean.class, CdiParamInjectHelperBean.class);
        start();
        assertTrue(selectGet(CdiBean1.class).callThis());
        assertTrue(selectGet(CdiBean1.class).getCdiHelperBean().callHelper());
        assertNull(selectGet(CdiBean1.class).getCdiHelperBean().field);
        assertNotNull(selectGet(CdiBean1.class).getCdiHelperBeanCdiParamInjectHelperBean().field);
    }

}
