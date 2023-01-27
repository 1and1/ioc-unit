package cdiunit5;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.contexts.jaxrs.SupportJaxRsEjbCdiUnit;

@ExtendWith(IocJUnit5Extension.class)
@SupportJaxRsEjbCdiUnit
public class TestJaxRs {

    @Inject
    private WebService webService;

    @Test
    public void testJaxRs() {
        Assertions.assertNotNull(webService.request);
        Assertions.assertNotNull(webService.response);
        Assertions.assertNotNull(webService.context);
        Assertions.assertNotNull(webService.uriInfo);
        Assertions.assertNotNull(webService.jaxRsRequest);
        Assertions.assertNotNull(webService.securityContext);
        Assertions.assertNotNull(webService.providers);
        Assertions.assertNotNull(webService.headers);
    }

    public static class WebService {
        @Context
        HttpServletRequest request;

        @Context
        HttpServletResponse response;

        @Context
        ServletContext context;

        @Context
        UriInfo uriInfo;

        @Context
        Request jaxRsRequest;

        @Context
        SecurityContext securityContext;

        @Context
        Providers providers;


        @Context
        HttpHeaders headers;

    }
}
