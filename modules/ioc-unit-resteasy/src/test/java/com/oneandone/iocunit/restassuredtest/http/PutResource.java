package com.oneandone.iocunit.restassuredtest.http;

import java.util.concurrent.atomic.AtomicLong;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * @author aschoerk
 */
@Path("")
public class PutResource {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/greetingPut")
    public Greeting greeting(@FormParam("name") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @PUT
    @Path("/stringBody")
    public String stringBody(String body) {
        return body;
    }

    @PUT
    @Path("/jsonReflect")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String jsonReflect(String body) {
        return body;
    }

}
