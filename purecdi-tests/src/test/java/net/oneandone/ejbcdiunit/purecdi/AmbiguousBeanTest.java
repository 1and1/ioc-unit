package net.oneandone.ejbcdiunit.purecdi;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class AmbiguousBeanTest extends TestBaseClass {

    @Override
    public Collection<String> getBeanClasses() {
        return Arrays.asList(CdiBean1.class.getName(), CdiHelperBean.class.getName(), CdiHelperBean2.class.getName());
    }

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
        if (deploymentException != null)
            throw deploymentException;
        assertTrue(container.instance().select(CdiBean1.class).get().callThis());
        assertTrue(container.instance().select(CdiBean1.class).get().getCdiHelperBean().callHelper());
    }

}
