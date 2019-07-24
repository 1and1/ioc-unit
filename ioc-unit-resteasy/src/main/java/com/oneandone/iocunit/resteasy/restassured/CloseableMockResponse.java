package com.oneandone.iocunit.resteasy.restassured;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.params.HttpParams;
import org.codehaus.groovy.util.ArrayIterator;
import org.jboss.resteasy.mock.MockHttpResponse;

/**
 * @author aschoerk
 */
public class CloseableMockResponse implements CloseableHttpResponse {
    MockHttpResponse mockHttpResponse;

    public CloseableMockResponse(final MockHttpResponse mockHttpResponse) {
        this.mockHttpResponse = mockHttpResponse;
    }

    public int getStatus() {
        return mockHttpResponse.getStatus();
    }

    public void setStatus(final int status) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public StatusLine getStatusLine() {
        return new StatusLine() {
            @Override
            public ProtocolVersion getProtocolVersion() {
                return new ProtocolVersion("http", 1, 1);
            }

            @Override
            public int getStatusCode() {
                return getStatus();
            }

            @Override
            public String getReasonPhrase() {
                return "No Reason given";
            }
        };
    }

    @Override
    public void setStatusLine(final StatusLine statusLine) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setStatusLine(final ProtocolVersion protocolVersion, final int i) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setStatusLine(final ProtocolVersion protocolVersion, final int i, final String s) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setStatusCode(final int i) throws IllegalStateException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setReasonPhrase(final String s) throws IllegalStateException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public HttpEntity getEntity() {
        return new HttpEntity() {
            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public boolean isChunked() {
                return false;
            }

            @Override
            public long getContentLength() {
                return mockHttpResponse.getContentAsString().length();
            }

            @Override
            public Header getContentType() {
                return new Header() {
                    @Override
                    public String getName() {
                        return "Content-Type";
                    }

                    @Override
                    public String getValue() {
                        final List<Object> accept = mockHttpResponse.getOutputHeaders().get("Content-Type");
                        if(accept != null && !accept.isEmpty()) {
                            return accept.get(0).toString();
                        }
                        else {
                            return "*/*";
                        }
                    }

                    @Override
                    public HeaderElement[] getElements() throws ParseException {
                        String name = getName();
                        String value = getValue();
                        return new HeaderElement[]{
                                new HeaderElement() {
                                    @Override
                                    public String getName() {
                                        return name;
                                    }

                                    @Override
                                    public String getValue() {
                                        return value;
                                    }

                                    @Override
                                    public NameValuePair[] getParameters() {
                                        return new NameValuePair[0];
                                    }

                                    @Override
                                    public NameValuePair getParameterByName(final String s) {
                                        throw new RuntimeException("not implemented");
                                    }

                                    @Override
                                    public int getParameterCount() {
                                        return 0;
                                    }

                                    @Override
                                    public NameValuePair getParameter(final int i) {
                                        throw new RuntimeException("not implemented");
                                    }
                                }
                        };
                    }
                };
            }

            @Override
            public Header getContentEncoding() {
                throw new RuntimeException("not implemented");
            }

            @Override
            public InputStream getContent() throws IOException, UnsupportedOperationException {
                return new ByteArrayInputStream(mockHttpResponse.getOutput());
            }

            @Override
            public void writeTo(final OutputStream outputStream) throws IOException {
                outputStream.write(mockHttpResponse.getOutput());
            }

            @Override
            public boolean isStreaming() {
                return false;
            }

            @Override
            public void consumeContent() throws IOException {

            }
        };
    }

    @Override
    public void setEntity(final HttpEntity httpEntity) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Locale getLocale() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setLocale(final Locale locale) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean containsHeader(final String s) {
        return mockHttpResponse.getOutputHeaders().containsKey(s);
    }

    @Override
    public Header[] getHeaders(final String s) {
        List<Object> tmpres = mockHttpResponse.getOutputHeaders().get(s);
        if(tmpres != null) {
            return headersFromValueList(s, tmpres);
        }
        else {
            return new Header[0];
        }
    }

    private Header[] headersFromValueList(final String s, final List<Object> tmpres) {
        Header[] res = new Header[tmpres.size()];
        int i = 0;
        for (Object o : tmpres) {
            res[i] = headerFromOutputHeader(s, o);
        }
        return res;
    }

    private Header headerFromOutputHeader(final String s, final Object o) {
        return new Header() {
            @Override
            public String getName() {
                return s;
            }

            @Override
            public String getValue() {
                return o.toString();
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                throw new RuntimeException("not implemented");
            }
        };
    }

    @Override
    public Header getFirstHeader(final String s) {
        final Header[] headers = getHeaders(s);
        return headers != null && headers.length > 0 ? headers[0] : null;
    }

    @Override
    public Header getLastHeader(final String s) {
        final Header[] headers = getHeaders(s);
        return headers != null && headers.length > 0 ? headers[headers.length - 1] : null;
    }

    @Override
    public Header[] getAllHeaders() {
        MultivaluedMap<String, Object> outputHeaders = mockHttpResponse.getOutputHeaders();
        List<Header> res = new ArrayList<>();
        for (Map.Entry<String, List<Object>> e : outputHeaders.entrySet()) {
            Header[] headers = headersFromValueList(e.getKey(), e.getValue());
            for (Header h : headers) {
                res.add(h);
            }
        }
        return res.toArray(new Header[res.size()]);
    }

    @Override
    public void addHeader(final Header header) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addHeader(final String s, final String s1) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setHeader(final Header header) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setHeader(final String s, final String s1) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setHeaders(final Header[] headers) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removeHeader(final Header header) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removeHeaders(final String s) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public HeaderIterator headerIterator() {
        return headerIteratorFromArray(getAllHeaders());
    }

    @Override
    public HeaderIterator headerIterator(final String s) {
        return headerIteratorFromArray(getHeaders(s));
    }

    private HeaderIterator headerIteratorFromArray(Header[] a) {
        ArrayIterator<Header> ah = new ArrayIterator<>(a);
        return new HeaderIterator() {
            @Override
            public boolean hasNext() {
                return ah.hasNext();
            }

            @Override
            public Header nextHeader() {
                return ah.next();
            }

            @Override
            public Object next() {
                return ah.next();
            }
        };
    }

    /**
     * @deprecated
     */
    @Override
    public HttpParams getParams() {
        throw new RuntimeException("not implemented");
    }

    /**
     * @param httpParams
     * @deprecated
     */
    @Override
    public void setParams(final HttpParams httpParams) {
        throw new RuntimeException("not implemented");
    }
}
