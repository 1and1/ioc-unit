package com.oneandone.iocunit.resteasytester.auth.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
