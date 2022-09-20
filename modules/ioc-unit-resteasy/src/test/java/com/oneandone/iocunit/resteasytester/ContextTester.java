package com.oneandone.iocunit.resteasytester;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URISyntaxException;

import javax.inject.Inject;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;
import com.oneandone.iocunit.resteasy.servlet.IocUnitServletContextHolder;
import com.oneandone.iocunit.resteasytester.resources.ResourceUsingDifferentContexts;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ResourceUsingDifferentContexts.class})
public class ContextTester {
    @Inject
    IocUnitResteasyDispatcher dispatcher;

    @Inject
    IocUnitServletContextHolder iocUnitServletContextHolder;

    @Test
    public void testServletContext() throws URISyntaxException {
        // just demonstrate how the servlet context can be initialized.
        iocUnitServletContextHolder.setServletContext(iocUnitServletContextHolder.getServletContext());
        MockHttpRequest request =
                MockHttpRequest.get("/contextstest/method1/SERVLETCONTEXT");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testUriInfo() throws URISyntaxException {
        MockHttpRequest request =
                MockHttpRequest.get("/contextstest/method1/URIINFO");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        assertEquals(200, response.getStatus());
    }
}
