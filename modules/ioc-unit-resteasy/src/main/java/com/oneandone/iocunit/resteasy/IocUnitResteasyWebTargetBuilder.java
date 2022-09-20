package com.oneandone.iocunit.resteasy;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

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
