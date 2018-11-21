package ejbcdiunit2.first;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;
import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

import ejbcdiunit2.first.producing.ProducingClass1;
import ejbcdiunit2.first.test1.Qualifier1A;
import ejbcdiunit2.first.test1.Test1A;
import ejbcdiunit2.first.test1.Test1B;
import ejbcdiunit2.first.test1.Test1Interface;

/**
 * @author aschoerk
 */
@RunWith(EjbCdiUnit2Runner.class)
@SutPackages({ Test1A.class, ProducingClass1.class })
@ExcludedClasses({ Test1B.class })
public class TestQualifiersWithPackagedef {
    @Inject
    Test1Interface test1Interface;

    @Inject
    @Qualifier1A
    Test1Interface test1InterfaceForTest1B;

    @Test
    public void canOptimizeInjecting() {
        assertNotNull(test1Interface);
        assertEquals("Test1A", test1Interface.call());
    }

    @Test
    public void canEvaluateQualifiersOptimizeInjecting() {
        assertNotNull(test1InterfaceForTest1B);
        assertEquals("Test1B", test1InterfaceForTest1B.call());
    }

}
