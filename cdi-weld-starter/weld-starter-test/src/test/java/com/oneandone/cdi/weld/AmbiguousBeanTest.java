package com.oneandone.cdi.weld;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.inject.Inject;

import org.junit.Test;
import org.mockito.Mock;

import com.oneandone.cdi.weldstarter.WrappedDeploymentException;

/**
 * @author aschoerk
 */
public class AmbiguousBeanTest extends WeldStarterTestsBase {


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


    @Test(expected = WrappedDeploymentException.class)
    public void test() {
        setBeanClasses(CdiBean1.class,
                CdiHelperBean.class, CdiHelperBean2.class);

        start();
    }

    @Alternative
    @Stereotype
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD })
    @interface StereoTypeAlt {

    }

    interface AInterface {

    }

    @ApplicationScoped
    static class Class1 {
        @Inject
        AInterface aInterface;
    }

    static class AImplementation implements AInterface {

    }

    @ApplicationScoped
    static class Class2 {

        @Inject
        AImplementation aImplementation;

        @Inject
        Class1 class1;

        @Produces
        // @StereoTypeAlt
        @Mock
        AInterface aInterface;

    }

    @Test
    public void alternativeFillsSuperClass() {
        setEnabledAlternativeStereotypes(StereoTypeAlt.class);
        setBeanClasses(AImplementation.class, Class1.class, Class2.class);
        start();
    }

}
