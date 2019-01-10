package alternatives;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.SutClasspaths;
import com.oneandone.cdi.tester.CdiUnit2Runner;

/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
@SutClasspaths({ CdiBean.class })
public class NonAlternativeTest {

    @Inject
    CdiBean cdiBean1;

    @Test
    public void test() {
        assertTrue(cdiBean1.callThis());
        assertTrue(cdiBean1.getCdiHelperBean().callHelper());
    }
}
