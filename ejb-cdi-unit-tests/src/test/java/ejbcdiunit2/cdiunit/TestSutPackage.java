package ejbcdiunit2.cdiunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

import ejbcdiunit2.cdiunit.test1.Test1A;
import ejbcdiunit2.cdiunit.test1.Test1Interface;

/**
 * @author aschoerk
 */
@RunWith(EjbCdiUnit2Runner.class)
@SutPackages({ Test1A.class })
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
