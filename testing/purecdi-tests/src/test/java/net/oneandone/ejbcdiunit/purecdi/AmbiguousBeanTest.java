package net.oneandone.ejbcdiunit.purecdi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class AmbiguousBeanTest extends WeldStarterTestBase {


    @ApplicationScoped
    static class CdiHelperBean {
        public boolean callHelper() {
            return true;
        }
    }


    @ApplicationScoped
    static class CdiHelperBean2 extends CdiHelperBean {
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


    @Test(expected = StarterDeploymentException.class)
    public void test() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class, CdiHelperBean2.class);

        start();
    }

}
