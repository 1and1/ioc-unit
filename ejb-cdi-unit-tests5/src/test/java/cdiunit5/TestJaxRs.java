package cdiunit5;

import com.oneandone.ejbcdiunit.jaxrs.SupportJaxRsEjbCdiUnit;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Providers;

@ExtendWith(JUnit5Extension.class)
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
