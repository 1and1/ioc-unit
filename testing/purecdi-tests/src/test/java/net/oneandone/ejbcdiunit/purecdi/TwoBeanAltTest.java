package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.junit.Test;
import org.mockito.Mockito;

import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

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





    @Test(expected = StarterDeploymentException.class)
    public void testDeploymentException() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class,
                CdiHelperBeanAlt.class);
        start();
    }

    @Test(expected = StarterDeploymentException.class)
    public void testDeploymentExceptionWithAltSet() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class,
                CdiHelperBeanAlt.class);
        setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
    }


    @Test
    public void testWithAlternative() {
        setBeanClasses(CdiBean1.class,
                DummyClass.class,
                Dummy2Class.class,
                CdiBean1.class,
                CdiHelperBean.class,
                CdiHelperBeanAlt.class);
        setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
        assertFalse(selectGet(CdiBean1.class).cdiHelperBean.callHelper());
    }

    @Test
    public void testWithAlternativeWithoutOrgClassAvailable() {
        setBeanClasses(CdiBean1.class,
                DummyClass.class,
                Dummy2Class.class,
                CdiBean1.class,
                CdiHelperBeanAlt.class);
        setAlternativeClasses(CdiHelperBeanAlt.class);
        start();
        assertFalse(selectGet(CdiBean1.class).cdiHelperBean.callHelper());
    }

    @Test(expected = StarterDeploymentException.class) // Alternative must be in beanClasses
    public void testWithAlternativeWithoutAltClassAvailable() {
        setBeanClasses(
                DummyClass.class,
                Dummy2Class.class,
                CdiBean1.class);
        setAlternativeClasses(CdiHelperBeanAlt.class);
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
