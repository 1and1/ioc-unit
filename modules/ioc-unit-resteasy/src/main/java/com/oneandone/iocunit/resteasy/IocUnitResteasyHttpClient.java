package com.oneandone.iocunit.resteasy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
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

import com.oneandone.iocunit.resteasy.restassured.CloseableMockResponse;

/**
 * HttpClient that does not send via Network, but sends via Resteasy-MockDispatcher
 **/
@IocUnitResteasy
public class IocUnitResteasyHttpClient extends AbstractHttpClient {

    public static final String LOCALHOST = "//localhost";

    @Inject
    Dispatcher dispatcher;

    Logger logger = LoggerFactory.getLogger(IocUnitResteasyHttpClient.class);

    public IocUnitResteasyHttpClient(Dispatcher dispatcher) {
        super(null, null);
        this.dispatcher = dispatcher;
    }

    public IocUnitResteasyHttpClient() {
        super(null, null);
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
        Set<Map.Entry<Class<?>, Object>> contextData = ResteasyProviderFactory.getContextDataMap().entrySet();
        ResteasyProviderFactory.clearContextData();
        try {
            MockHttpRequest mockRequest = createMockHttpRequestFromRequest(request);
            addHeadersFromRequest(request, mockRequest);
            MockHttpResponse response = new MockHttpResponse();
            Map<Class<?>, Object> map = ResteasyProviderFactory.getContextDataMap();
            if(DispatcherDelegate.getSecurityContext() != null) {
                map.put(SecurityContext.class, DispatcherDelegate.getSecurityContext());
            }
            dispatcher.invoke(mockRequest, response);
            return new CloseableMockResponse(response);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            contextData.forEach(d ->
                    ResteasyProviderFactory.getContextDataMap().put(
                            d.getKey(),
                            d.getValue()));
        }

    }

    private void addHeadersFromRequest(final HttpRequest request, final MockHttpRequest mockRequest) {
        for (Header h : request.getAllHeaders()) {
            if(h != null) {
                if(h.getName().equals("Accept")) {
                    mockRequest.accept(h.getValue());
                }
                if(h.getName().equals("Cookie")) {
                    String[] valueparts = h.getValue().split("=|; ");
                    for (int i = 0; i < valueparts.length; i++, i++) {
                        mockRequest.cookie(valueparts[i], valueparts[i + 1]);
                    }
                }
                mockRequest.header(h.getName(), h.getValue());
            }
        }
    }

    private MockHttpRequest createMockHttpRequestFromRequest(final HttpRequest request) throws URISyntaxException, IOException {
        String method = request.getRequestLine().getMethod();
        HttpEntity entity = null;
        if (request instanceof HttpEntityEnclosingRequestBase) {
            entity = ((HttpEntityEnclosingRequestBase) request).getEntity();
        }

        String uri = request.getRequestLine().getUri();
        int localhostPos = uri.indexOf(LOCALHOST);
        if(localhostPos >= 0) {
            int slashpos = uri.indexOf("/",localhostPos + LOCALHOST.length());
            uri = uri.substring(slashpos);
        }
        MockHttpRequest mockRequest = null;

        if (entity != null)  {
            mockRequest = buildMockHttpRequest(entity, uri, method);
        } else {
            mockRequest = MockHttpRequest.create(method, uri);
        }

        return mockRequest;
    }

    private MockHttpRequest buildMockHttpRequest(final HttpEntity entity, final String uri, final String put) throws URISyntaxException, IOException {
        MockHttpRequest mockRequest;
        mockRequest = MockHttpRequest.create(put, uri);
        if(entity != null) {
            if(entity.getContentType() != null) {
                mockRequest.contentType(entity.getContentType().getValue());
            }
            try (InputStream c = entity.getContent()) {
                if(c != null) {
                    mockRequest.content(c);
                }
            }
        }
        return mockRequest;
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


    @Override
    public void close() {
        dispatcher = null;
        logger = null;
        super.close();
    }
}