package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class TwoBeanWithGenericsTest extends TestBaseClass {

    @Override
    public Collection<String> getBeanClasses() {
        return Arrays.asList(CdiBean1.class.getName(), CdiHelperBean.class.getName(), CdiParamInjectHelperBean.class.getName());
    }

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
        assertTrue(container.select(CdiBean1.class).get().callThis());
        assertTrue(container.select(CdiBean1.class).get().getCdiHelperBean().callHelper());
        assertNull(container.select(CdiBean1.class).get().getCdiHelperBean().field);
        assertNotNull(container.select(CdiBean1.class).get().getCdiHelperBeanCdiParamInjectHelperBean().field);
    }

}
