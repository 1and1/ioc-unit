package alternatives;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({CdiBean.class, CdiHelperBeanAlt.class})
public class NonAlternativeTest {

    @Inject
    CdiBean cdiBean1;

    @Test
    public void test() {
        assertTrue(cdiBean1.callThis());
        assertTrue(cdiBean1.getCdiHelperBean().callHelper());
    }
}
