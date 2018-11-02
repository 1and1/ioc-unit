package net.oneandone.ejbcdiunit.purecdi;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class TwoBeanAltSettingUpTest extends WeldStarterTestBase {



    public interface CdiHelperBeanIntf {
        boolean callHelper();
    }

    static class Dummy2Class {

    }

    static class DummyClass {

        @Inject
        Dummy2Class dummy2Class;
    }


    @Alternative
    static class CdiHelperBeanAlt implements CdiHelperBeanIntf {
        @Override
        public boolean callHelper() {
            return false;
        }
    }

    static class CdiHelperBean implements CdiHelperBeanIntf {

        @Inject
        private DummyClass value;

        @Override
        public boolean callHelper() {
            return true;
        }
    }

    static class CdiBean1 {
        @Inject
        CdiHelperBeanIntf cdiHelperBean;

        public boolean callThis() {
            return true;
        }

        public CdiHelperBeanIntf getCdiHelperBean() {
            return cdiHelperBean;
        }
    }

    @Test(expected = DeploymentException.class)
    public void test() {
        setBeanClasses(CdiBean1.class, CdiHelperBean.class, CdiHelperBeanAlt.class);
        setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
    }

}
