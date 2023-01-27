package com.oneandone.iocunitejb.example7;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Created by aschoerk on 28.06.17.
 */
@Path("/simplerest")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public interface ServiceIntf {


    @GET
    @Path("/numbers/5")
    Response returnFive();

    @POST
    @Path("/entities/entity1")
    Response newEntity1(@QueryParam("intvalue") int intValue, @QueryParam("stringvalue")String stringValue);

    @GET
    @Path("/entities/entity1/string")
    Response getStringValueFor(@QueryParam("id")long id);

    @GET
    @Path("/entities/entity1/int")
    Response getIntValueFor(@QueryParam("id")long id);

}
