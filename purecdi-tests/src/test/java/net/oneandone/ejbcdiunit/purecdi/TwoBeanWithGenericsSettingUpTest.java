package net.oneandone.ejbcdiunit.purecdi;

import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * @author aschoerk
 */
public class TwoBeanWithGenericsSettingUpTest extends SettingUpTestBaseClass {

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
        if (deploymentException != null)
            throw deploymentException;
        assertTrue(getContainer().select(CdiBean1.class).get().callThis());
        assertTrue(getContainer().select(CdiBean1.class).get().getCdiHelperBean().callHelper());
        assertNull(getContainer().select(CdiBean1.class).get().getCdiHelperBean().field);
        assertNotNull(getContainer().select(CdiBean1.class).get().getCdiHelperBeanCdiParamInjectHelperBean().field);
    }

}
