package resteasy.auth;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author aschoerk
 */
@RolesAllowed("MayUseResourceIfNotOtherwiseDefinedByMethod")
@Path("/restpath2")
public class SecuredResource {

    @GET
    @Path("/method1")
    public Response method1() {
        return Response.ok().build();
    }

    @GET
    @Path("/method2")
    @RolesAllowed("maycallmethod2")
    public Response method2() {
        return Response.ok().build();
    }

    @GET
    @Path("/method3")
    public Response method3() {
        return Response.ok().build();
    }
}
