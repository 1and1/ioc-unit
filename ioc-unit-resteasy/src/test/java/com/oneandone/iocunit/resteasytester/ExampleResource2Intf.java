package com.oneandone.iocunit.resteasytester;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/restpath2")
public interface ExampleResource2Intf {
    @GET
    @Path("/method1")
    public Response method1();

}
