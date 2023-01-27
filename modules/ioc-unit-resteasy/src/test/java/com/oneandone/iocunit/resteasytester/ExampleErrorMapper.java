package com.oneandone.iocunit.resteasytester;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author aschoerk
 */
@Provider
@ApplicationScoped
public class ExampleErrorMapper implements ExceptionMapper<RuntimeException> {
    @Context
    UriInfo uriInfo;

    @Inject
    InjectTest injectTest;

    @Override
    public Response toResponse(final RuntimeException e) {
        assert uriInfo != null;
        injectTest.callInjectTest();
        return Response.status(400).build();
    }
}
