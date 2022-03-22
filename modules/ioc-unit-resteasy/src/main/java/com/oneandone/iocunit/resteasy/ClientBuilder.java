package com.oneandone.iocunit.resteasy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 * use {@link IocUnitResteasyClientBuilder}
 */
@Deprecated
@ApplicationScoped
public class ClientBuilder extends IocUnitResteasyClientBuilder {

    @Produces
    public javax.ws.rs.client.ClientBuilder createClientBuilder() {
        return super.createClientBuilder();
    }
}
