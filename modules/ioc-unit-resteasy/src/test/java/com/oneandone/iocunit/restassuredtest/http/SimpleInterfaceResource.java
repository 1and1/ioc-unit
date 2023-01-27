package com.oneandone.iocunit.restassuredtest.http;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("")
public interface SimpleInterfaceResource     {
    @Path("/check")
    @GET
    Response check();
}
