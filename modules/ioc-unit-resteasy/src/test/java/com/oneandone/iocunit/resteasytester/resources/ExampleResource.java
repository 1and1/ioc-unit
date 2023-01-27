package com.oneandone.iocunit.resteasytester.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.oneandone.iocunit.resteasytester.InjectTest;

/**
 * @author aschoerk
 */
@Path("/restpath")
public class ExampleResource {
    @Inject
    InjectTest injectTest;

    @GET
    @Path("/method1")
    public Response method1() {
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/postdto")
    public Response postDto(ExampleDto dto) {
        return Response.ok(dto).build();
    }

    @GET
    @Path("/error")
    public Response error() {
        throw new RuntimeException();
    }

    @GET
    @Path("/injecttest")
    public Response injectTest() {
        injectTest.callInjectTest();
        return Response.ok().build();
    }
}
