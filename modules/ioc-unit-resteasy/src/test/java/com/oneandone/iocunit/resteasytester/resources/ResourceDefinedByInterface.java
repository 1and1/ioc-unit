package com.oneandone.iocunit.resteasytester.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
public class ResourceDefinedByInterface implements ResourceDefinedByInterfaceIntf {

    @GET
    @Path("/method1")
    public Response method1() {
        return Response.ok().build();
    }

}
