package net.oneandone.ejbcdiunit.purecdi;

import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * @author aschoerk
 */
public class TwoBeanSettingUpTest extends SettingUpTestBaseClass {

    @Override
    public Collection<String> getBeanClasses() {
        return Arrays.asList(CdiBean1.class.getName(), CdiHelperBean.class.getName());
    }

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
        if (deploymentException != null)
            throw deploymentException;
        assertTrue(getContainer().instance().select(CdiBean1.class).get().callThis());
        assertTrue(getContainer().instance().select(CdiBean1.class).get().getCdiHelperBean().callHelper());
    }

}
