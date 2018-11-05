package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author aschoerk
 */
public class TwoBeanAltTest extends WeldStarterTestBase {

    public interface CdiHelperBeanIntf {
        boolean callHelper();
    }

    static class Dummy2Class {
        boolean returnTrue() {
            return true;
        }

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

        @Produces
        Dummy2Class produceDummy2Class() {
            return Mockito.mock(Dummy2Class.class);
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
    public void testDeploymentException() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class,
                CdiHelperBeanAlt.class);
        start();
    }

    @Test(expected = DeploymentException.class)
    public void testDeploymentExceptionWithAltSet() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class,
                CdiHelperBeanAlt.class);
        setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
    }


    @Test
    public void testWithAlternative() {
        weldSetup.setBeanClasses(CdiBean1.class,
                DummyClass.class,
                Dummy2Class.class,
                CdiBean1.class,
                CdiHelperBean.class,
                CdiHelperBeanAlt.class);
        weldSetup.setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
        assertFalse(selectGet(CdiBean1.class).cdiHelperBean.callHelper());
    }

    @Test
    public void testWithAlternativeWithoutOrgClassAvailable() {
        weldSetup.setBeanClasses(CdiBean1.class,
                DummyClass.class,
                Dummy2Class.class,
                CdiBean1.class,
                CdiHelperBeanAlt.class);
        weldSetup.setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
        assertFalse(selectGet(CdiBean1.class).cdiHelperBean.callHelper());
    }

    @Test(expected = DeploymentException.class) // Alternative must be in beanClasses
    public void testWithAlternativeWithoutAltClassAvailable() {
        weldSetup.setBeanClasses(
                DummyClass.class,
                Dummy2Class.class,
                CdiBean1.class);
        weldSetup.setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
    }

    @Test
    public void testWithMock() {
        setBeanClasses(
                DummyClass.class,
                CdiBean1.class,
                CdiHelperBeanAlt.class);
        setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
        assertFalse(selectGet(CdiBean1.class).cdiHelperBean.callHelper());
        assertFalse(selectGet(Dummy2Class.class).returnTrue());
    }

    @Test
    public void testWithMockCompetingAgainstOriginal() {
        setBeanClasses(
                DummyClass.class,
                Dummy2Class.class, // ambiguus dependency should lead to error
                CdiBean1.class,
                CdiHelperBeanAlt.class);
        setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
        assertFalse(selectGet(CdiBean1.class).cdiHelperBean.callHelper());
        assertFalse(selectGet(Dummy2Class.class).returnTrue());
    }

    @Test
    public void testWithMockCompetingAgainstOriginaWithoutAlternative() {
        setBeanClasses(
                DummyClass.class,
                Dummy2Class.class,
                CdiBean1.class,
                CdiHelperBean.class);
        // setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
        assertTrue(selectGet(CdiBean1.class).cdiHelperBean.callHelper());
        assertTrue(selectGet(Dummy2Class.class).returnTrue());
    }
}
