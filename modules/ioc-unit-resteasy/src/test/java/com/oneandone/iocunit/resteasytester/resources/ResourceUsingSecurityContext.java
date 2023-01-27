package com.oneandone.iocunit.resteasytester.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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
