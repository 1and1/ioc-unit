package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean1;
import com.oneandone.ejbcdiunit5.junit5.beans.AppScopedBean2;
import com.oneandone.ejbcdiunit5.junit5.beans.BaseBean;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ AppScopedBean1.class, AppScopedBean2.class })
public class InstanceTestNested {

    static int calls = 0;

    @Inject
    Instance<BaseBean> baseBeans;

    @Nested
    class TestAlsoCalledInInnerClasses {
        @RepeatedTest(5)
        public void test(RepetitionInfo repetitionInfo) {
            assertNotNull(baseBeans);
            Instance<AppScopedBean1> beanInstance1 = baseBeans.select(AppScopedBean1.class);
            AppScopedBean1 bean1 = beanInstance1.iterator().next();
            assertEquals(bean1.getValue(), AppScopedBean1.APPSCOPED_BEAN_INIT_VALUE);
            bean1.setValue(bean1.getValue() + 1);
            Instance<AppScopedBean2> beanInstance2 = baseBeans.select(AppScopedBean2.class);
            AppScopedBean2 bean2 = beanInstance2.iterator().next();
            calls++;
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestAlsoCalledInInnerClassesLifecyclePerClass {

        int localCalls = 0;

        @RepeatedTest(5)
        public void test(RepetitionInfo repetitionInfo) {
            assertNotNull(baseBeans);
            // assertEquals(baseBeans.stream().count(), 2);
            Instance<AppScopedBean1> beanInstance1 = baseBeans.select(AppScopedBean1.class);
            AppScopedBean1 bean1 = beanInstance1.iterator().next();
            // because of per class, no new beans created
            assertEquals(bean1.getValue(), AppScopedBean1.APPSCOPED_BEAN_INIT_VALUE + localCalls);
            bean1.setValue(bean1.getValue() + 1);
            Instance<AppScopedBean2> beanInstance2 = baseBeans.select(AppScopedBean2.class);
            AppScopedBean2 bean2 = beanInstance2.iterator().next();
            calls++;
            localCalls++;

            assertEquals(localCalls, repetitionInfo.getCurrentRepetition());
        }
    }

    @AfterAll
    public static void afterAll() {
        assertEquals(calls, 10);
    }

}

