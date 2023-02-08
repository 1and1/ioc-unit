package org.oneandone.iocunit.rest.minimal_rest_war.restassured_1;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/rest")
public class RestResource {

    @GET
    @Path("/dto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDto1(@PathParam("id") long id) {

        return Response.ok()
                .entity("{\"id\":\"" + id + "\", \"content\": \"dummycontent\" }")
                .build();
    }
}
