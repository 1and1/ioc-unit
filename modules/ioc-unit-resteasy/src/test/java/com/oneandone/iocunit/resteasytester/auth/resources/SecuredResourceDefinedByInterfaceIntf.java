package com.oneandone.iocunit.resteasytester.auth.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/restpath3")
@RolesAllowed("mayuseclass")
public interface SecuredResourceDefinedByInterfaceIntf {
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
    public Response method3();
}
