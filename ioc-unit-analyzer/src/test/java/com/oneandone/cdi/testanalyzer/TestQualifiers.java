package com.oneandone.cdi.testanalyzer;

import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.testanalyzer.producing.ProducingClass1;
import com.oneandone.cdi.testanalyzer.test1.Qualifier1A;
import com.oneandone.cdi.testanalyzer.test1.Test1Interface;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestQualifiers extends BaseTest {
    @SutClasses(ProducingClass1.class)
    static class TestClass {
        @Inject
        Test1Interface test1Interface;

        @Inject
        @Qualifier1A
        Test1Interface test1InterfaceForTest1B;
    }

    @Test
    public void canInjectFromQualifiedProducer() {
        createTest(TestClass.class);
        assertEquals(2, toBeStarted.size());
        assertTrue(toBeStarted.contains(ProducingClass1.class));
    }
}
