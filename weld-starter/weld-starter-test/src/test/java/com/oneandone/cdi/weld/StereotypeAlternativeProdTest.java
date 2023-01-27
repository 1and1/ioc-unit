package com.oneandone.cdi.weld;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Stereotype;
import jakarta.inject.Inject;

import org.junit.Test;
import org.mockito.Mockito;

import com.oneandone.cdi.weldstarter.StarterDeploymentException;

/**
 * @author aschoerk
 */
public class StereotypeAlternativeProdTest extends WeldStarterTestsBase {


    static class DummyClass {

    }


    static class CdiHelperBean {

        DummyClass value;

        @Inject
        CdiHelperBean(DummyClass value) {
            this.value = value;
        }

        public boolean callHelper() {
            return true;
        }
    }

    @Alternative
    @Stereotype
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface StereoTypeAlt {

    }

    static class TestResources {
        @Produces
        @StereoTypeAlt
        CdiHelperBean cdiHelperBean() {
            return Mockito.mock(CdiHelperBean.class);
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

    // WrappedDeploymentException is produced in spite of CdiHelperBean is replaced by a Producer.
    @Test(expected = StarterDeploymentException.class)
    public void test() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class,
                TestResources.class);
        setEnabledAlternativeStereotypes(StereoTypeAlt.class);
        start();
    }

    @Test
    public void testByAddingDependency() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class,
                DummyClass.class,
                TestResources.class);
        setEnabledAlternativeStereotypes(StereoTypeAlt.class);
        start();
        assertTrue(selectGet(CdiBean1.class).callThis());
        assertFalse(selectGet(CdiBean1.class).getCdiHelperBean().callHelper());
    }

    @Test
    public void testByRemovingOriginal() {
        setBeanClasses(CdiBean1.class,
                TestResources.class);
        setEnabledAlternativeStereotypes(StereoTypeAlt.class);
        start();
        assertTrue(selectGet(CdiBean1.class).callThis());
        assertFalse(selectGet(CdiBean1.class).getCdiHelperBean().callHelper());
    }

}
