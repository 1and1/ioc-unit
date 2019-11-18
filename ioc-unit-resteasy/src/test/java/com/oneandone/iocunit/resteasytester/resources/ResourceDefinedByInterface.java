package com.oneandone.iocunit.resteasytester.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
