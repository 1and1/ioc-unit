package net.oneandone.ejbcdiunit.relbuilder.beans;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class TestBeanWithProducerInject {
    @Produces
    TestBeanWithInjectedField createTestBean1(BeanToBeInjected beanToBeInjected) {
        TestBeanWithInjectedField res = new TestBeanWithInjectedField();
        res.setBeanToBeInjected(beanToBeInjected);
        return res;
    }
}
