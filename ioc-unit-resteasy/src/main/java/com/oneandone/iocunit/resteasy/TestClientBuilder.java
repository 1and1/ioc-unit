package com.oneandone.iocunit.resteasy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.core.Dispatcher;

import com.oneandone.iocunit.resteasy.restassured.TestHttpClient;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class TestClientBuilder {
    @Inject
    private Dispatcher dispatcher;

    @Produces
    public ResteasyClientBuilder createClientBuilder() {
        return new ResteasyClientBuilder()
                .httpEngine(new ApacheHttpClient4Engine(new TestHttpClient(dispatcher)));
    }
}
