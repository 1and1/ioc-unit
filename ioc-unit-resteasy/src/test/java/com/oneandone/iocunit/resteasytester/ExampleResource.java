package com.oneandone.iocunit.resteasytester;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/restpath")
public class ExampleResource {
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
}
