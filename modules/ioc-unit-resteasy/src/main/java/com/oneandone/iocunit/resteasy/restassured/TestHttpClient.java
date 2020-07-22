package com.oneandone.iocunit.resteasy.restassured;

import org.jboss.resteasy.core.Dispatcher;

import com.oneandone.iocunit.resteasy.IocUnitResteasyHttpClient;

/**
 * @author aschoerk
 * use IocUnitResteasyHttpClient
 */
@Deprecated
public class TestHttpClient extends IocUnitResteasyHttpClient {
    public TestHttpClient(Dispatcher dispatcher) {
        super(dispatcher);}

    public TestHttpClient() {
        super();
    }
}
