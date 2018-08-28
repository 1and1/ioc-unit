package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean2;

/**
 * @author aschoerk
 */
public class DerivedClassTest extends TwoMethodTest {

    @BeforeAll
    public static void beforeAll() {
        testCalled = 0;
    }

    @Inject
    AppScopedBean2 appScopedBean2;

    @RepeatedTest(6)
    public void testRepetition() {
        checkAppScopedBean2();
    }

    private void checkAppScopedBean2() {
        // check if injection has been done
        assertNotNull(appScopedBean2);
        assertTrue(appScopedBean2.toString().contains(AppScopedBean2.class.getSimpleName()));
    }


    @ParameterizedTest
    @ValueSource(strings = { "a", "b" })
    public void checkParamTest(String s) {
        checkAppScopedBean2();
    }


    @AfterAll
    // name must be the same as in superclass, otherwise both will be called.
    public static void afterAll() {
        // check if superclasstests (2), 6 repetitions and 2 params have been called
        assertEquals(testCalled, 10);
    }
}
