package com.oneandone.a.ejbcdiunit2.relbuilder.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class TestBeanWithInjectedField {
    @Inject
    BeanToBeInjected beanToBeInjected;

    public void setBeanToBeInjected(final BeanToBeInjected beanToBeInjected) {
        this.beanToBeInjected = beanToBeInjected;
    }
}
