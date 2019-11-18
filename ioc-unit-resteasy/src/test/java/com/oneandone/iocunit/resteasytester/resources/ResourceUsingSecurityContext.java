package com.oneandone.iocunit.resteasytester.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author aschoerk
 */
@Path("/restpathsecure")
public class ResourceUsingSecurityContext {

    @Context
    SecurityContext securityContext;

    @GET
    @Path("/method1")
    public Response method1() {
        if (!securityContext.isUserInRole("norole"))
            return Response.ok().build();
        else
            return Response.status(510).build();
    }
}
