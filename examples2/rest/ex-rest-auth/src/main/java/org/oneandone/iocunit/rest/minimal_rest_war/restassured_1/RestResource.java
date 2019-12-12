package org.oneandone.iocunit.rest.minimal_rest_war.restassured_1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
