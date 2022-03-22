package com.oneandone.iocunit.resteasytester;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.security.Principal;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;
import com.oneandone.iocunit.resteasytester.resources.ResourceUsingSecurityContext;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ResourceUsingSecurityContext.class})
public class UsingSecurityContextTest {
    @Inject
    IocUnitResteasyDispatcher dispatcher;

    @Produces
    SecurityContext createSecurityContext() {
        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public boolean isUserInRole(final String s) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        };
    }

    @Test
    public void callMethod1() throws URISyntaxException {
        // ResteasyProviderFactory.getContextDataMap().put(SecurityContext.class, );

        MockHttpRequest request =
                MockHttpRequest.get("/restpathsecure/method1");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        assertEquals(200, response.getStatus());
    }
}
