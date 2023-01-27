package com.oneandone.iocunit.resteasytester.resources;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;

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
    @Path("/method1/{test}")
    public Response method1(@PathParam("test") String test) {
        if(test.equals("SERVLETCONTEXT")) {
            String path = servletContext.getContextPath();
        }
        if(test.equals("URIINFO")) {
            String path = uriInfo.getPath();
            if(!path.contains("/method1/URIINFO")) {
                throw new AssertionError("expected uriinfocontext");
            }
        }
        return Response.ok().build();
    }

}
