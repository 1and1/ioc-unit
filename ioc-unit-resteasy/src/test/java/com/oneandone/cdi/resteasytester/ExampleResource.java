package com.oneandone.cdi.resteasytester;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/restpath")
public class ExampleResource {
    @GET
    @Path("/method1")
    public Response method1() {
        return Response.ok().build();
    }

    @GET
    @Path("/error")
    public Response error() {
        throw new RuntimeException();
    }

}
