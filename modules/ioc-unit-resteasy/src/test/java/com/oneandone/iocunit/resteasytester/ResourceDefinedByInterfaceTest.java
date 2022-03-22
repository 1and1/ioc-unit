package com.oneandone.iocunit.resteasytester;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import javax.inject.Inject;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;
import com.oneandone.iocunit.resteasytester.resources.ResourceDefinedByInterface;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({ExampleErrorMapper.class, ResourceDefinedByInterface.class})
public class ResourceDefinedByInterfaceTest {
    @Inject
    IocUnitResteasyDispatcher dispatcher;

    @Test
    public void testGreen() throws URISyntaxException {
        MockHttpRequest request =
                MockHttpRequest.get("/restpath2/method1");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        assertEquals(200, response.getStatus());
    }

}
