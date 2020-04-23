package com.oneandone.iocunit.resteasy;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * @author aschoerk
 */
public class TestWebTarget {
    @Inject
    ClientBuilder clientBuilder;

    @Produces
    WebTarget webTarget() {
        return clientBuilder.build().target("/");
    }
}
