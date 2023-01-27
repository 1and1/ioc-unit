package com.oneandone.iocunit.analyzer.instance.sut;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
public class Container {
    @Inject
    Instance<Intf> impls;
}
