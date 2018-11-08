package com.oneandone.a.ejbcdiunit2.relbuilder.beans;

import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class ConstructedTestBean {

    BeanToBeInjected beanToBeInjected;

    @Inject
    public ConstructedTestBean(BeanToBeInjected beanToBeInjected) {
        this.beanToBeInjected = beanToBeInjected;
    }
}
