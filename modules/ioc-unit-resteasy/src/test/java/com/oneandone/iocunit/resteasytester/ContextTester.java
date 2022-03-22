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
import com.oneandone.iocunit.resteasytester.resources.ResourceUsingDifferentContexts;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ResourceUsingDifferentContexts.class})
public class ContextTester {
    @Inject
    IocUnitResteasyDispatcher dispatcher;

    @Test
    public void test() throws URISyntaxException {
        MockHttpRequest request =
                MockHttpRequest.get("/contextstest/method1");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        assertEquals(200, response.getStatus());
    }
}
