package com.oneandone.iocunit.resteasy.restassured;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.AbstractHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class TestHttpClient extends AbstractHttpClient {

    public static final String LOCALHOST_8080 = "//localhost:8080";

    Dispatcher dispatcher;

    Logger logger = LoggerFactory.getLogger(TestHttpClient.class);

    public TestHttpClient(Dispatcher dispatcher) {
        super(null, null);
        this.dispatcher = dispatcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableHttpResponse execute(
            final HttpHost target,
            final HttpRequest request) throws IOException, ClientProtocolException {
        return innerExecute(target, request, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableHttpResponse execute(
            final HttpHost target,
            final HttpRequest request,
            final HttpContext context) throws IOException, ClientProtocolException {
        return innerExecute(target, request, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableHttpResponse execute(
            final HttpUriRequest request,
            final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(request, "HTTP request");
        return innerExecute(null, request, context);
    }


    protected CloseableHttpResponse innerExecute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {

        try {
            String uri = request.getRequestLine().getUri();
            int localhostPos = uri.indexOf(LOCALHOST_8080);
            if (localhostPos >= 0) {
                uri = uri.substring(localhostPos + LOCALHOST_8080.length());
            }
            MockHttpRequest mockRequest = null;
            if(request instanceof HttpPut) {
                final HttpEntity entity = ((HttpPut) request).getEntity();
                mockRequest = MockHttpRequest.create(HttpMethod.PUT, uri);
                mockRequest.contentType(entity.getContentType().getValue());
                mockRequest.content(entity.getContent());
            }
            else if(request instanceof HttpGet) {
                mockRequest = MockHttpRequest.create(HttpMethod.GET, uri);
            }
            else if(request instanceof HttpPost) {
                final HttpEntity entity = ((HttpPost) request).getEntity();
                mockRequest = MockHttpRequest.create(HttpMethod.POST, uri);
                if(entity != null) {
                    mockRequest.contentType(entity.getContentType().getValue());
                    mockRequest.content(entity.getContent());
                }
            }
            else if(request instanceof HttpDelete) {
                mockRequest = MockHttpRequest.create(HttpMethod.DELETE, uri);
            }
            else if(request instanceof HttpPatch) {
                mockRequest = MockHttpRequest.create("PATCH", uri);
            }
            for (Header h : request.getAllHeaders()) {
                if(h != null) {
                    if(h.getName().equals("Accept")) {
                        mockRequest.accept(h.getValue());
                    }
                    mockRequest.header(h.getName(), h.getValue());
                }
            }
            MockHttpResponse response = new MockHttpResponse();
            Map<Class<?>, Object> map = ResteasyProviderFactory.getContextDataMap();
            dispatcher.invoke(mockRequest, response);
            return new CloseableMockResponse(response);


        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    protected HttpParams createHttpParams() {
        return new AbstractHttpParams() {

            @Override
            public Object getParameter(final String s) {
                return null;
            }

            @Override
            public HttpParams setParameter(final String s, final Object o) {
                return null;
            }

            @Override
            public HttpParams copy() {
                return this;
            }

            @Override
            public boolean removeParameter(final String s) {
                return false;
            }
        };
    }

    @Override
    protected BasicHttpProcessor createHttpProcessor() {
        return new BasicHttpProcessor();
    }


}