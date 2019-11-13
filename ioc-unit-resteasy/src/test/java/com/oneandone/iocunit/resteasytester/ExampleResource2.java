package com.oneandone.iocunit.resteasytester;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author aschoerk
 */
public class ExampleResource2 implements ExampleResource2Intf {
    @GET
    @Path("/method1")
    public Response method1() {
        return Response.ok().build();
    }

}
