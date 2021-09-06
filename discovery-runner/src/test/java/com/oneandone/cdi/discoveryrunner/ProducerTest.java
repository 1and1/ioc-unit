package com.oneandone.cdi.discoveryrunner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author aschoerk
 */
@ExtendWith(WeldDiscoveryExtension.class)
@ApplicationScoped
public class ProducerTest extends ProducerTestBase {

    @Inject
    ToBeInjected toBeInjected;

    @Test
    public void test() {
        Assertions.assertEquals(1, toBeInjected.innerProduced.getI());
    }
}
