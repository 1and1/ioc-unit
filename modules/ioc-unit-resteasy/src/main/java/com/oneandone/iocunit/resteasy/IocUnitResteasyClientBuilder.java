package com.oneandone.iocunit.resteasy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.core.Dispatcher;

/**
 * Extension/Replacement of ResteasyClientBuilder, using a HttpClient that routes all Requests to
 * MockDispatcherFactory-Dispatcher
 */
@ApplicationScoped
public class IocUnitResteasyClientBuilder {
    @Inject
    private Dispatcher dispatcher;

    @Produces
    public ResteasyClientBuilder createClientBuilder() {
        return new ResteasyClientBuilder()
                .httpEngine(new ApacheHttpClient4Engine(new IocUnitResteasyHttpClient(dispatcher)));
    }
}
