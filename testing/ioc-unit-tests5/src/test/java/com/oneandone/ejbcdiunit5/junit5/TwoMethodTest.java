package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean1;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

import jakarta.inject.Inject;

@ExtendWith(IocJUnit5Extension.class)
@SutPackages(AppScopedBean1.class)
class TwoMethodTest {

    private static final int VALUE_INIT = -10;
    protected static int testCalled = 0;
    int value = -10;

    @Inject
    private AppScopedBean1 appScopedBean1;

    @BeforeAll
    static void beforeAll() {
        testCalled = 0;
    }

    @AfterAll
    static void afterAll() {
        assertEquals(2, testCalled);
    }

    @AfterEach
    void afterEach() {
        testCalled++;
        value++;
    }

    @Test
    void test1() {
        sameSituationInBothMethods();
    }

    @Test
    void test2() {
        sameSituationInBothMethods();
    }

    private void sameSituationInBothMethods() {
        // CDI Container did correct injects
        assertNotNull(appScopedBean1);
        assertTrue(appScopedBean1.toString().contains(AppScopedBean1.class.getSimpleName()));

        // CDI container restarted before each test
        assertEquals(appScopedBean1.getValue(), AppScopedBean1.APPSCOPED_BEAN_INIT_VALUE);
        assertEquals(VALUE_INIT, value);
    }
}
