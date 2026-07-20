package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean2;

/**
 * Extends {@link BaseContainerIsolationTest} to confirm that per-test CDI container isolation
 * also holds for inherited test methods, as well as for repeated and parameterized invocations.
 * Additionally injects {@link AppScopedBean2}, a bean declared only at this subclass level, to
 * prove that beans added in a derived test class are also freshly (re-)injected on every
 * invocation - not just beans inherited from the superclass.
 */
class InheritedContainerIsolationTest extends BaseContainerIsolationTest {

    @Inject
    private AppScopedBean2 bean2;

    private void assertFreshBean2ThenPoison() {
        assertEquals(AppScopedBean2.APPSCOPED_BEAN_INIT_VALUE, bean2.getValue(),
                "subclass-declared bean should also start fresh on every invocation");
        bean2.setValue(-1);
    }

    @RepeatedTest(3)
    void repeatedTest() {
        assertFreshBeanThenPoison();
        assertFreshBean2ThenPoison();
    }

    @ParameterizedTest
    @ValueSource(strings = { "a", "b" })
    void parameterizedTest(String value) {
        assertFreshBeanThenPoison();
        assertFreshBean2ThenPoison();
    }
}
