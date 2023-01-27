package com.oneandone.cdi.weld.beans;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
public abstract class AbstractClassToBeInjected {

    @Inject
    ToInclude toInclude;
    @Produces
    ToInclude tmp = new ToInclude(); // no produces clash with excluded ToExclude

    public void test() {
        if (toInclude.count == 0)
            throw new RuntimeException();
    }

}
