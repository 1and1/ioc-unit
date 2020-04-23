package com.oneandone.iocunit.resteasytester.resources;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

/**
 * @author aschoerk
 */

@Path("/contextstest")
public class ResourceUsingDifferentContexts {

    @Context
    ServletContext servletContext;

    @Context
    HttpServletRequest httpServletRequest;

    @Context
    HttpServletResponse httpServletResponse;

    @Context
    HttpSession httpSession;

    @Context
    SecurityContext securityContext;

    @Context
    Request request;

    @Context
    UriInfo uriInfo;

    @Context
    Providers provider;

    @GET
    @Path("/method1")
    public Response method1() {
        return Response.ok().build();
    }

}
