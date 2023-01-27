package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean1;
import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean2;
import com.oneandone.ejbcdiunit5.junit5.beans.BaseBean;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@SutClasses({ AppScopedBean1.class, AppScopedBean2.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstanceLifecyclePerClassTest {

    static int calls = 0;

    @Inject
    Instance<BaseBean> baseBeans;

    @RepeatedTest(5)
    public void test(RepetitionInfo repetitionInfo) {
        assertNotNull(baseBeans);
        Instance<AppScopedBean1> beanInstance1 = baseBeans.select(AppScopedBean1.class);
        AppScopedBean1 bean1 = beanInstance1.iterator().next();
        assertEquals(bean1.getValue(), AppScopedBean1.APPSCOPED_BEAN_INIT_VALUE + calls);
        bean1.setValue(bean1.getValue() + 1);
        Instance<AppScopedBean2> beanInstance2 = baseBeans.select(AppScopedBean2.class);
        AppScopedBean2 bean2 = beanInstance2.iterator().next();
        calls++;
        assertEquals(calls, repetitionInfo.getCurrentRepetition());
    }

    @AfterAll
    public static void afterAll() {
        assertEquals(calls, 5);
    }

}

