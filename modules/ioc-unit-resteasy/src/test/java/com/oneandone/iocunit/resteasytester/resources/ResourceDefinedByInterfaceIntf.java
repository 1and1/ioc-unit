package com.oneandone.iocunit.resteasytester.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/restpath2")
public interface ResourceDefinedByInterfaceIntf {
    @GET
    @Path("/method1")
    public Response method1();

}
