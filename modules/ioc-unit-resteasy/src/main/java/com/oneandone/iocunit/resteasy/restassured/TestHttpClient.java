package com.oneandone.iocunit.resteasy.restassured;

import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;
import com.oneandone.iocunit.resteasy.IocUnitResteasyHttpClient;

/**
 * @author aschoerk
 * use IocUnitResteasyHttpClient
 */
@Deprecated
public class TestHttpClient extends IocUnitResteasyHttpClient {
    public TestHttpClient(IocUnitResteasyDispatcher dispatcher) {
        super(dispatcher);
    }

    public TestHttpClient() {
        super();
    }
}
