package com.oneandone.iocunit.resteasy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * @author aschoerk
 * use {@link IocUnitResteasyClientBuilder}
 */
@Deprecated
@ApplicationScoped
public class ClientBuilder extends IocUnitResteasyClientBuilder {

    @Produces
    public ResteasyClientBuilder createClientBuilder() {
        return super.createClientBuilder();
    }
}
