package com.oneandone.iocunit.analyzer;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.producing.ProducingClass1;
import com.oneandone.iocunit.analyzer.test1.Qualifier1A;
import com.oneandone.iocunit.analyzer.test1.Test1Interface;

import org.junit.Test;

import javax.inject.Inject;

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
