package iocunit.cdiunit;

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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.contexts.jaxrs.SupportJaxRsEjbCdiUnit;

@RunWith(IocUnitRunner.class)
@SupportJaxRsEjbCdiUnit
public class TestJaxRs extends BaseTest {

    @Inject
    private WebService webService;

    @Test
    public void testJaxRs() {
        Assert.assertNotNull(webService.request);
        Assert.assertNotNull(webService.response);
        Assert.assertNotNull(webService.context);
        Assert.assertNotNull(webService.uriInfo);
        Assert.assertNotNull(webService.jaxRsRequest);
        Assert.assertNotNull(webService.securityContext);
        Assert.assertNotNull(webService.providers);
        Assert.assertNotNull(webService.headers);
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
