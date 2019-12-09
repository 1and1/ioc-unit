package com.oneandone.iocunit.resteasytester.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/restpath2")
public interface SecuredResourceDefinedByInterfaceIntf {
    @GET
    @Path("/method1")
    @RolesAllowed("maycallmethod1")
    public Response method1();
    
    public Response method2();

    public Response method3();
}
