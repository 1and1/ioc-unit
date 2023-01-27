package com.oneandone.iocunit.resteasy;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

/**
 * @author aschoerk
 */
public class IocUnitResteasyWebTargetBuilder {
    @Inject
    protected IocUnitResteasyClientBuilder clientBuilder;

    @Produces
    WebTarget webTarget() {
        Client client = clientBuilder.createClientBuilder().build();
        return client.target("/");
    }
}
