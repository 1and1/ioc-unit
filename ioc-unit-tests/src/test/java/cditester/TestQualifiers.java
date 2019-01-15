package cditester;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.IocUnitRunner;

import cditester.producing.ProducingClass1;
import cditester.test1.Qualifier1A;
import cditester.test1.Test1Interface;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ ProducingClass1.class })
public class TestQualifiers {
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
