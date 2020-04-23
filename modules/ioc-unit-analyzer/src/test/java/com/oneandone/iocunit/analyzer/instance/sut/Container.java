package com.oneandone.iocunit.analyzer.instance.sut;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class Container {
    @Inject
    Instance<Intf> impls;
}
