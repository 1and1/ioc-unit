package cdiunittests.alternatives;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ CdiBean.class, CdiHelperBean.class })
public class NonAlternativeTest {

    @Inject
    CdiBean cdiBean1;

    @Test
    public void test() {
        assertTrue(cdiBean1.callThis());
        assertTrue(cdiBean1.getCdiHelperBean().callHelper());
    }
}
