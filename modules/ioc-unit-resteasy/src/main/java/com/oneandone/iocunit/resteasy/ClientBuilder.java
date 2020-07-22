package com.oneandone.iocunit.resteasy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

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
