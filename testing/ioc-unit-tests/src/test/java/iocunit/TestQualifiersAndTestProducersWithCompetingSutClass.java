package iocunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import jakarta.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;

import iocunit.producing.ProducingClass1;
import iocunit.test1.Qualifier1A;
import iocunit.test1.Test1A;
import iocunit.test1.Test1B;
import iocunit.test1.Test1Interface;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ ProducingClass1.class })
@SutClasses({ Test1A.class, Test1B.class })
public class TestQualifiersAndTestProducersWithCompetingSutClass {
    @Inject
    Test1Interface test1Interface;

    @Inject
    @Qualifier1A
    Test1Interface test1InterfaceForTest1B;

    @Test(expected = StarterDeploymentException.class) // SutClasses must compete against Producer
    public void canOptimizeInjecting() {
        assertNotNull(test1Interface);
        assertEquals("Test1A", test1Interface.call());
    }


}
