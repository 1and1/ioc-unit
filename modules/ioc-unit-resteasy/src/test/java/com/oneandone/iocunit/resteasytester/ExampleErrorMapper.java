package com.oneandone.iocunit.resteasytester;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
