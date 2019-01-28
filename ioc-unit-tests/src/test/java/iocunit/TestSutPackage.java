package iocunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

import iocunit.test1.Test1A;
import iocunit.test1.Test1Interface;

/**
 * @author aschoerk
 * <p>
 * was TestSutPackage. Meanwhile the assumption is, that in available SutClasses ambiguities should be
 * intended.
 */
@RunWith(IocUnitRunner.class)
@SutPackages({Test1A.class})
public class TestSutPackage {
    @Inject
    Test1A test1;

    @Inject
    Test1Interface test1Interface;


    @Test
    public void test() {
        assertNotNull(test1);
        assertEquals("Test1A", test1.call());
    }

    @Test
    public void canOptimizeInjecting() {
        assertNotNull(test1Interface);
        assertEquals("Test1A", test1Interface.call());
    }

}
