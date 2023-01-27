package com.oneandone.iocunit.restassuredtest.http;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/redirect")
public class RedirectResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response redirect() {
        return Response.status(Response.Status.MOVED_PERMANENTLY)
                .header("Location", "http://localhost:8080/redirect/1")
                .entity("{ \"id\" : 1 }").build();
    }
}
