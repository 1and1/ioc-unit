package com.oneandone.iocunit.resteasytester.auth.resources;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/restpath4")
public interface SecuredResourceDefinedByInterface2Intf {
    @GET
    @Path("/method1")
    @RolesAllowed("maycallmethod1")
    public Response method1();

    @GET
    @Path("/method2")
    @RolesAllowed("maycallmethod2")
    public Response method2();

    @GET
    @Path("/method3")
    @DenyAll
    public Response method3();
}
