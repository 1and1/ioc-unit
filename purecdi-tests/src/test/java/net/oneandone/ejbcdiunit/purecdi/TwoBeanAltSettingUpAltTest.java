package net.oneandone.ejbcdiunit.purecdi;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.junit.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author aschoerk
 */
public class TwoBeanAltSettingUpAltTest  {

    public interface CdiHelperBeanIntf {
        boolean callHelper();
    }

    static class Dummy2Class {

    }

    static class DummyClass {

        @Inject
        Dummy2Class dummy2Class;
    }

    static class MockingClass {

        @Produces
        Dummy2Class produceDummy2Class() {
            return Mockito.mock(Dummy2Class.class);
        }
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



    static class WeldSetupClass implements WeldSetup {
        public Collection<String> beanClasses = Collections.EMPTY_LIST;

        @Override
        public Collection<String> getBeanClasses() {
            return beanClasses;
        }

        @Override
        public List<Metadata<String>> getAlternativeClasses() {
            return Arrays.asList(new Metadata<String>() {
                @Override
                public String getValue() {
                    return CdiHelperBeanAlt.class.getName();
                }

                @Override
                public String getLocation() {
                    return "In Testcode";
                }
            });
        }

        @Override
        public List<Metadata<String>> getEnabledAlternativeStereotypes() {
            return Collections.emptyList();
        }

        @Override
        public void setDeploymentException(DeploymentException deploymentException) {

        }
    };
    WeldSetupClass weldSetup = new WeldSetupClass();
    WeldStarter weldStarter = new WeldStarter();

    @Test(expected = DeploymentException.class)
    public void testDeploymentException() {
        weldSetup.beanClasses = Arrays.asList(CdiBean1.class.getName(),
                // DummyClass.class.getName(),
                CdiHelperBean.class.getName(),
                CdiHelperBeanAlt.class.getName());
        weldStarter.start(weldSetup);
        weldStarter.tearDown();
    }

    @Test
    public void test() {

        weldSetup.beanClasses = Arrays.asList(CdiBean1.class.getName(),
                DummyClass.class.getName(),
                Dummy2Class.class.getName(),
                CdiHelperBean.class.getName(),
                CdiHelperBeanAlt.class.getName());
        weldStarter.start(weldSetup);
        weldStarter.tearDown();
    }

    @Test
    public void testWithMock() {

        weldSetup.beanClasses = Arrays.asList(CdiBean1.class.getName(),
                DummyClass.class.getName(),
                MockingClass.class.getName(),
                CdiHelperBean.class.getName(),
                CdiHelperBeanAlt.class.getName());
        weldStarter.start(weldSetup);
        weldStarter.tearDown();
    }
}
