package com.oneandone.iocunit.resteasytester;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author aschoerk
 */
@Provider
public class ExampleErrorMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(final RuntimeException e) {
        return Response.status(400).build();
    }
}
