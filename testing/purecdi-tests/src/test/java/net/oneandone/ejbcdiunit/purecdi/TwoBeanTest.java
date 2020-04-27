package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class TwoBeanTest extends WeldStarterTestBase {


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
