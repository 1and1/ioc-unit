package com.oneandone.iocunit.restassuredtest.http;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("/header")
public class HeaderResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response header(@HeaderParam("headerName") String headerValue, @HeaderParam("User-Agent") String userAgent) {
        final String entity = "{\"headerName\" : \"" + headerValue + "\", \"user-agent\" : \"" + userAgent + "\"}";
        return Response
                .ok()
                .entity(entity)
                .header("Content-Length", entity.length())
                .build();
    }
}
