package alternatives;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.EnabledAlternatives;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.CdiUnit2Runner;

/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
@SutPackages({ CdiBean.class })
@EnabledAlternatives({ CdiHelperBeanAlt.class })
public class AlternativeTest {

    @Inject
    CdiBean cdiBean1;

    @Test
    public void test() {
        assertTrue(cdiBean1.callThis());
        assertFalse(cdiBean1.getCdiHelperBean().callHelper());
    }
}
