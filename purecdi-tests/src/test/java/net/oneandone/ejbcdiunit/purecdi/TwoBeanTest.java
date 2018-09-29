package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class TwoBeanTest extends TestBaseClass {

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
        assertTrue(container.instance().select(CdiBean1.class).get().callThis());
        assertTrue(container.instance().select(CdiBean1.class).get().getCdiHelperBean().callHelper());
    }

}
