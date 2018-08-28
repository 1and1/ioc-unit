package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean1;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TwoMethodLifecyclePerClassTest {

    static int testCalled = 0; // used to find out how many testmethods where called

    @Inject
    private AppScopedBean1 appScopedBean1;

    int value = -10;

    @AfterEach
    public void afterEach() {
        testCalled++;
        value++;
    }

    private void sameSituationInBothMethods() {
        // CDI did inject
        assertNotNull(appScopedBean1);
        assertTrue(appScopedBean1.toString().contains(AppScopedBean1.class.getSimpleName()));

        // CDI did inject only once - classlevel
        assertEquals(appScopedBean1.getValue(), AppScopedBean1.APPSCOPED_BEAN_INIT_VALUE + testCalled);
        appScopedBean1.setValue(appScopedBean1.getValue() + 1);
        assertEquals(value, -10 + testCalled);
    }

    @Test
    public void test() {
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
