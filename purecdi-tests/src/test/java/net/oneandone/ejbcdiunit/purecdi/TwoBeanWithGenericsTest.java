package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class TwoBeanWithGenericsTest extends WeldStarterTestBase {

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
        assertTrue(getContainer().select(CdiBean1.class).get().callThis());
        assertTrue(getContainer().select(CdiBean1.class).get().getCdiHelperBean().callHelper());
        assertNull(getContainer().select(CdiBean1.class).get().getCdiHelperBean().field);
        assertNotNull(getContainer().select(CdiBean1.class).get().getCdiHelperBeanCdiParamInjectHelperBean().field);
    }

}
