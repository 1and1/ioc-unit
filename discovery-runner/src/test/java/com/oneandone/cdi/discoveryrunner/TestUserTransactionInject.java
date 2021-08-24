package com.oneandone.cdi.discoveryrunner;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WeldDiscoveryExtension.class)
public class TestUserTransactionInject {
    @Inject
    UserTransaction userTransaction;

    @Test
    public void testing() {
        Assertions.assertNotNull(userTransaction);
    }
}
