package com.oneandone.iocunit.resteasy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;

import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;

/**
 * Extension/Replacement of ResteasyClientBuilder, using a HttpClient that routes all Requests to
 * IocUnitMockDispatcherFactory-IocUnitResteasyDispatcher
 */
@ApplicationScoped
public class IocUnitResteasyClientBuilder {
    @Inject
    private IocUnitResteasyDispatcher dispatcher;

    private List<IocUnitResteasyHttpClient> httpClients = new ArrayList<>();

    @PreDestroy
    public void preDestroy() {
        httpClients.stream().forEach(IocUnitResteasyHttpClient::close);
    }

    @Produces
    public ResteasyClientBuilder createClientBuilder() {
        IocUnitResteasyHttpClient httpClient = new IocUnitResteasyHttpClient(dispatcher);
        httpClients.add(httpClient);
        javax.ws.rs.client.ClientBuilder builder = javax.ws.rs.client.ClientBuilder.newBuilder();
        try {
            Method engineSetter = builder.getClass().getMethod("httpEngine", ClientHttpEngine.class);
            engineSetter.invoke(builder, new ApacheHttpClient43Engine(httpClient));
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return (ResteasyClientBuilder) builder;
    }
}
