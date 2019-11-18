package com.oneandone.iocunit.resteasytester.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/restpath2")
public interface ResourceDefinedByInterfaceIntf {
    @GET
    @Path("/method1")
    public Response method1();

}
