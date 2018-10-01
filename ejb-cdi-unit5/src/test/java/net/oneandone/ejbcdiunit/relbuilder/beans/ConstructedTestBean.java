package net.oneandone.ejbcdiunit.relbuilder.beans;

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
