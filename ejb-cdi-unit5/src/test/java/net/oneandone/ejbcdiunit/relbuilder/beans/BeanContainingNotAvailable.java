package net.oneandone.ejbcdiunit.relbuilder.beans;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class BeanContainingNotAvailable {
    @Inject
    @Any
    BeanWithNotAvailableInjects beanWithNotAvailableInjects;
}
