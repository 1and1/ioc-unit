package com.oneandone.cdi.discoveryrunner;

import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class ToBeInjected {
    @Inject
    ProducerTestBase.InnerProduced innerProduced;
}
