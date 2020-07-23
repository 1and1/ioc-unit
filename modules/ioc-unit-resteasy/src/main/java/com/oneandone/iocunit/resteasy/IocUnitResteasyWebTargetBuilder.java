package com.oneandone.iocunit.resteasy;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;

/**
 * @author aschoerk
 */
public class IocUnitResteasyWebTargetBuilder {
    @Inject
    protected IocUnitResteasyClientBuilder clientBuilder;

    @Produces
    WebTarget webTarget() {
        return clientBuilder.createClientBuilder().build().target("/");
    }
}
