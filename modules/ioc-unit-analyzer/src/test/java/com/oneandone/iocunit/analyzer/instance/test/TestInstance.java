package com.oneandone.iocunit.analyzer.instance.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.instance.sut.Container;
import com.oneandone.iocunit.analyzer.instance.sut.Impl1;
import com.oneandone.iocunit.analyzer.instance.sut.Impl2;

/**
 * @author aschoerk
 */
public class TestInstance extends BaseTest {
    @Test
    public void test1() {
        createTest(TestClassSample.class);
        assertTrue(toBeStarted.contains(Container.class));
        assertTrue(toBeStarted.contains(Impl1.class));
        assertTrue(toBeStarted.contains(Impl2.class));
    }
}
