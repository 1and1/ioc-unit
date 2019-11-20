package com.oneandone.iocunit.restassuredtest.http;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@Path("")
public interface SimpleInterfaceResource     {
    @Path("/check")
    @GET
    Response check();
}
