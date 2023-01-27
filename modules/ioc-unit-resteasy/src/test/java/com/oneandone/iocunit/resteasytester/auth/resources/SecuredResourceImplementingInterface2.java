package com.oneandone.iocunit.resteasytester.auth.resources;

import jakarta.ws.rs.core.Response;

/**
 * @author aschoerk
 */
public class SecuredResourceImplementingInterface2 implements SecuredResourceDefinedByInterface2Intf {
    @Override
    public Response method1() {
        return Response.ok().build();
    }

    @Override
    public Response method2() {
        return Response.ok().build();
    }

    @Override
    public Response method3() {
        return Response.ok().build();
    }
}
