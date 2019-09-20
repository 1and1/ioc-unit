package com.oneandone.iocunit.resteasytester;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author aschoerk
 */
@Provider
public class ExampleErrorMapper implements ExceptionMapper<RuntimeException> {
    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(final RuntimeException e) {
        assert uriInfo != null;
        return Response.status(400).build();
    }
}
