package org.oneandone.iocunit.rest.dto_polymorphy.service;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.oneandone.iocunit.rest.dto_polymorphy.dto.ComplexDto;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.ComplexDtoWithSetters;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.abstractsuper.DtoSuper;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.second.DtoInterface2;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.Dto1;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.Dto2;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.typename.DtoInterface;

/**
 * @author aschoerk
 */
@Path("/rest")
public class RestResource {

    @GET
    @Path("/dto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDto1(@PathParam("id") int id) {
        DtoInterface result = id % 2 == 0 ? new Dto1(id, "dto1") : new Dto2(id, "dto2");

        return Response.ok()
                .entity(result)
                .build();
    }

    @POST
    @Path("/dto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mirror(DtoInterface dto) {

        return Response.ok()
                .entity(dto)
                .build();
    }

    @POST
    @Path("/bdto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mirror(DtoInterface2 dto) {

        return Response.ok()
                .entity(dto)
                .build();
    }
    @POST
    @Path("/complexdto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mirror(ComplexDto dto) {

        return Response.ok()
                .entity(dto)
                .build();
    }
    @POST
    @Path("/complexdtowithsetters")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mirror(ComplexDtoWithSetters dto) {

        return Response.ok()
                .entity(dto)
                .build();
    }

    @POST
    @Path("/dtosuper")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mirror(DtoSuper dto) {

        return Response.ok()
                .entity(dto)
                .build();
    }



}
