package com.oneandone.ejbcdiunit.example7;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
