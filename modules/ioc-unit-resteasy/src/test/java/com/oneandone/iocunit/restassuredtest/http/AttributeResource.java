package com.oneandone.iocunit.restassuredtest.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

/**
 * @author aschoerk
 */
@Path("/attribute")
public class AttributeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response attribute(@Context HttpServletRequest request) {
        Collection<String> attributes = new ArrayList<String>();
        for (String attributeName : Collections.list(request.getAttributeNames())) {
            attributes.add("\"" + attributeName + "\": \"" + request.getAttribute(attributeName) + "\"");
        }

        return Response.ok().entity("{" + StringUtils.join(attributes, ", ") + "}").build();
    }


}
