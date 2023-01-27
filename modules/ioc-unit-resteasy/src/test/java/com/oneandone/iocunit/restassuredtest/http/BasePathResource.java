package com.oneandone.iocunit.restassuredtest.http;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;


/**
 * @author aschoerk
 */
@Path("/")
public class BasePathResource {
    private static final String template = "Hello, %s!";

    @GET
    @Path("/my-path/greetingPath")
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting greeting(
            @QueryParam("name") @DefaultValue("World") String name) {
        return new Greeting(0, String.format(template, name));
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting greeting2(
            @QueryParam("name") @DefaultValue("World") String name) {
        return new Greeting(1, String.format(template, name));
    }
}
