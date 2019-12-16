package com.oneandone.iocunit.resteasytester;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.core.Dispatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.resteasy.restassured.TestHttpClient;
import com.oneandone.iocunit.resteasytester.resources.ExampleResource;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ExampleErrorMapper.class, ExampleResource.class})
public class ClientBuilderTest {

    @Inject
    ClientBuilder clientBuilder;

    @Inject
    private Dispatcher dispatcher;

    @Produces
    public ClientBuilder createClientBuilder() {
        return new ResteasyClientBuilder()
                .httpEngine(new ApacheHttpClient4Engine(new TestHttpClient(dispatcher)));
    }


    @Test
    public void test() {
        WebTarget wt = clientBuilder.build().target("/");
        Response invoke = wt
                .path("/restpath/method1")
                .request(MediaType.APPLICATION_JSON)
                .buildGet().invoke();
        assertThat(invoke.getStatus(), is(200));
    }
}
