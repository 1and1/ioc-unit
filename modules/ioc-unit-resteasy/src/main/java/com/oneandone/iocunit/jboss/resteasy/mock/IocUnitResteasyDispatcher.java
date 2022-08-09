package com.oneandone.iocunit.jboss.resteasy.mock;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public interface IocUnitResteasyDispatcher {
    ResteasyProviderFactory getProviderFactory();

    Registry getRegistry();

    void invoke(HttpRequest var1, HttpResponse var2);

    Response internalInvocation(HttpRequest var1, HttpResponse var2, Object var3);

    void addHttpPreprocessor(HttpRequestPreprocessor var1);

    Map<Class, Object> getDefaultContextObjects();

    void setUp();
}
