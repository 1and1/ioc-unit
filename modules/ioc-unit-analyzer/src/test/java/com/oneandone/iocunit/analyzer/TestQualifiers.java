package com.oneandone.iocunit.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.producing.ProducingClass1;
import com.oneandone.iocunit.analyzer.test1.Qualifier1A;
import com.oneandone.iocunit.analyzer.test1.Test1Interface;

public class TestQualifiers extends BaseTest {
    @SutClasses(ProducingClass1.class)
    static class TestClass extends BaseClass {
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
