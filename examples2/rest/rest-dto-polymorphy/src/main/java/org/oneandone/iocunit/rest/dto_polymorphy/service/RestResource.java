package org.oneandone.iocunit.rest.dto_polymorphy.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.oneandone.iocunit.rest.dto_polymorphy.dto.ComplexDto;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.ComplexDtoWithSetters;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.abstractsuper.DtoSuper;
import org.oneandone.iocunit.rest.dto_polymorphy.dto.notpolymorph.NpDTo;
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
    public Response mirror(DtoInterface dto, @Context UriInfo uriInfo) {
        assert uriInfo != null;
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

    @POST
    @Path("/dtoalt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mirror(NpDTo dto) {

        return Response.ok()
                .entity(dto)
                .build();
    }



}
