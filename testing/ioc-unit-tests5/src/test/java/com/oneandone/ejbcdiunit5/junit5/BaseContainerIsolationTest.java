package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean1;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

/**
 * Base class providing the shared "bean starts fresh, then gets poisoned" assertion used by
 * {@link InheritedContainerIsolationTest} to prove that container-per-test isolation also holds
 * for inherited, repeated and parameterized test methods.
 */
@ExtendWith(IocJUnit5Extension.class)
@SutPackages(AppScopedBean1.class)
abstract class BaseContainerIsolationTest {

    @Inject
    private AppScopedBean1 bean;

    void assertFreshBeanThenPoison() {
        assertEquals(AppScopedBean1.APPSCOPED_BEAN_INIT_VALUE, bean.getValue(),
                "bean should start fresh - CDI container must be recreated per test invocation");
        bean.setValue(-1);
    }

    @Test
    void inheritedTest() {
        assertFreshBeanThenPoison();
    }
}
