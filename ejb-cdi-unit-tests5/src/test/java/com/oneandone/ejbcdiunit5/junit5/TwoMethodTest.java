package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean1;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
public class TwoMethodTest {

    public static final int VALUE_INIT = -10;
    static int testCalled = 0;

    @Inject
    private AppScopedBean1 appScopedBean1;

    int value = -10;

    @AfterEach
    public void afterEach() {
        testCalled++;
        value++;
    }

    private void sameSituationInBothMethods() {
        // CDI Container did correct injects
        assertNotNull(appScopedBean1);
        assertTrue(appScopedBean1.toString().contains(AppScopedBean1.class.getSimpleName()));

        // CDI container restarted before each test
        assertEquals(appScopedBean1.getValue(), AppScopedBean1.APPSCOPED_BEAN_INIT_VALUE);
        assertEquals(value, VALUE_INIT);
    }

    @Test
    public void test1() {
        sameSituationInBothMethods();
    }

    @Test
    public void test2() {
        sameSituationInBothMethods();
    }

    @AfterAll
    public static void afterAll() {
        assertEquals(testCalled, 2);
    }
}
