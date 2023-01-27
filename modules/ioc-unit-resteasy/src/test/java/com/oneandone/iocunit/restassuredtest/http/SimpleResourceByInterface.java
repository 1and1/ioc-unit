package com.oneandone.iocunit.restassuredtest.http;

import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
public class SimpleResourceByInterface implements SimpleInterfaceResource {
    @Override
    public Response check() {
        return Response.ok().build();
    }
}
