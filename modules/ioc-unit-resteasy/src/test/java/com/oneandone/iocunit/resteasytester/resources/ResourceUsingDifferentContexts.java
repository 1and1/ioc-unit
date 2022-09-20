package com.oneandone.iocunit.resteasytester.resources;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
