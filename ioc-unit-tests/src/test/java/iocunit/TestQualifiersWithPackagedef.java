package iocunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocUnitRunner;

import iocunit.producing.ProducingClass1;
import iocunit.test1.Qualifier1A;
import iocunit.test1.Test1A;
import iocunit.test1.Test1Interface;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutPackages({ Test1A.class, ProducingClass1.class })
public class TestQualifiersWithPackagedef {
    @Inject
    Test1Interface test1Interface;

    @Inject
    Test1Interface test1Interface2;

    @Inject
    Test1A test1A;

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
