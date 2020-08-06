package com.oneandone.iocunit.resteasy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
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

    private List<IocUnitResteasyHttpClient> httpClients = new ArrayList<>();

    @PreDestroy
    public void preDestroy() {
        httpClients.stream().forEach(IocUnitResteasyHttpClient::close);
    }

    @Produces
    public ResteasyClientBuilder createClientBuilder() {
        IocUnitResteasyHttpClient httpClient = new IocUnitResteasyHttpClient(dispatcher);
        httpClients.add(httpClient);
        return new ResteasyClientBuilder()
                .httpEngine(new ApacheHttpClient4Engine(httpClient));
    }
}
