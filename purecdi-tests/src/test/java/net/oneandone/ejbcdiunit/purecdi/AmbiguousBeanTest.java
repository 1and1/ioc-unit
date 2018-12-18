package net.oneandone.ejbcdiunit.purecdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;

import org.junit.Test;

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


    @Test(expected = DeploymentException.class)
    public void test() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class, CdiHelperBean2.class);

        start();
    }

}
