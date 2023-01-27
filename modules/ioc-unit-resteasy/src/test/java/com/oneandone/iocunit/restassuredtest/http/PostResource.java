package com.oneandone.iocunit.restassuredtest.http;

import java.util.concurrent.atomic.AtomicLong;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * @author aschoerk
 */
@Path("")
public class PostResource {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    // @RequestMapping(value = "/greetingPost", method = POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/greetingPost")
    public Greeting greeting(@FormParam("name") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    // @RequestMapping(value = "/stringBody", method = POST)
    @POST
    @Path("/stringBody")
    public String stringBody(String body) {
        return body;
    }

    @POST
    @Path("/jsonReflect")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String jsonReflect(String body) {
        return body;
    }

}
