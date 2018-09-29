

package alternatives;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ AltTest2.CdiBean1.class, AltTest2.CdiHelperBean.class })
@ActivatedAlternatives({ AltTest2.CdiHelperBeanAlt.class })
public class AltTest2 {

    public interface CdiHelperBeanIntf {
        boolean callHelper();
    }

    static class DummyClass {

    }

    @Alternative
    public static class CdiHelperBeanAlt implements CdiHelperBeanIntf {
        @Override
        public boolean callHelper() {
            return false;
        }
    }

    static class CdiHelperBean implements CdiHelperBeanIntf {

        private DummyClass value;

        @Inject
        CdiHelperBean(DummyClass dummyClass) {
            this.value = dummyClass;
        }

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

    @Inject
    CdiBean1 cdiBean1;

    @Test
    public void test() {
        assertTrue(cdiBean1.callThis());
        assertFalse(cdiBean1.getCdiHelperBean().callHelper());
    }
}

