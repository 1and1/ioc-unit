package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean1;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

/**
 * Directly verifies that ioc-unit recreates the CDI container for every single test invocation:
 * each repetition poisons the {@link jakarta.enterprise.context.ApplicationScoped} bean's state,
 * and the next repetition must see the bean freshly initialized again. If the container were
 * reused across invocations (as a real CDI application would), the second repetition would fail.
 */
@ExtendWith(IocJUnit5Extension.class)
@SutPackages(AppScopedBean1.class)
class PerTestContainerIsolationTest {

    @Inject
    private AppScopedBean1 bean;

    @RepeatedTest(3)
    void beanStateDoesNotLeakBetweenInvocations() {
        assertEquals(AppScopedBean1.APPSCOPED_BEAN_INIT_VALUE, bean.getValue(),
                "bean should start fresh - CDI container must be recreated per test invocation");
        bean.setValue(-1);
    }
}
