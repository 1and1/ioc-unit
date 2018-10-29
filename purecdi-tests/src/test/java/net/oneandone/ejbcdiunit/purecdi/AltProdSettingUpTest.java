package net.oneandone.ejbcdiunit.purecdi;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.junit.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author aschoerk
 */
public class AltProdSettingUpTest extends SettingUpTestBaseClass {

    @Override
    public Collection<String> getBeanClasses() {
        return Arrays.asList(CdiBean1.class.getName(),
                CdiHelperBean.class.getName(),
                TestResources.class.getName());
    }


    @Override
    public List<Metadata<String>> getEnabledAlternativeStereotypes() {
        return Arrays.asList(new Metadata<String>() {
            @Override
            public String getValue() {
                return AltProdSettingUpTest.StereoTypeAlt.class.getName();
            }

            @Override
            public String getLocation() {
                return "In Testcode";
            }
        });
    }

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

    @Test(expected = DeploymentException.class)
    public void test() {
        if (deploymentException != null)
            throw deploymentException;
        assertTrue(getContainer().instance().select(CdiBean1.class).get().callThis());
        assertFalse(getContainer().instance().select(CdiBean1.class).get().getCdiHelperBean().callHelper());
    }

}
