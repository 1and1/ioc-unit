package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.inject.Inject;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author aschoerk
 */
public class AltProdTest extends TestBaseClass {

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
                return AltProdTest.StereoTypeAlt.class.getName();
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

    @Test(expected = NullPointerException.class)
    public void test() {
        assertTrue(container.instance().select(CdiBean1.class).get().callThis());
        assertFalse(container.instance().select(CdiBean1.class).get().getCdiHelperBean().callHelper());
    }

}
