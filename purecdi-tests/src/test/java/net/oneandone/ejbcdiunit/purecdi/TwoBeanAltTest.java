package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.junit.Test;

/**
 * @author aschoerk
 */
public class TwoBeanAltTest extends TestBaseClass {

    @Override
    public Collection<String> getBeanClasses() {
        return Arrays.asList(CdiBean1.class.getName(),
                // DummyClass.class.getName(),
                CdiHelperBean.class.getName(),
                CdiHelperBeanAlt.class.getName());
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

    @Test
    public void test() {
        assertNotNull(this.deploymentException);
    }

}
