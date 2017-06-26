package com.oneandone.ejbcdiunit.resourcesimulators;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.w3c.dom.Element;

/**
 * Provide a dummy for  WebServiceContext-Resources
 *
 * @author aschoerk
 */
@ApplicationScoped
public class WebServiceContextSimulation implements WebServiceContext {

    /**
     * Dummy Http-Response to be returned from WebServiceContextSimulation in MessageContext
     */
    public static class MockHttpServletResponse implements HttpServletResponse {

        private int status = HttpServletResponse.SC_OK;

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setStatus(int sc) {
            status = sc;
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public Collection<String> getHeaders(final String name) {
            return null;
        }

        @Override
        public Collection<String> getHeaderNames() {
            return null;
        }

        @Override
        public void flushBuffer() throws IOException {
            // dummy
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void setBufferSize(int size) {
            // dummy
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {
            // dummy
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public void setContentType(String type) {
            // dummy
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public void setLocale(Locale loc) {
            // dummy
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return null;
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
            // dummy
        }

        @Override
        public void resetBuffer() {
            // dummy
        }

        @Override
        public void setContentLength(int len) {
            // dummy
        }

        @Override
        public void setContentLengthLong(long len) {
            // dummy
        }

        @Override
        public void addCookie(Cookie cookie) {
            // dummy

        }

        @Override
        public void addDateHeader(String name, long date) {
            // dummy
        }

        @Override
        public void addHeader(String name, String value) {
            // dummy
        }

        @Override
        public void addIntHeader(String name, int value) {
            // dummy
        }

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return null;
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return null;
        }

        @Override
        public String encodeURL(String url) {
            return null;
        }

        @Override
        public String encodeUrl(String url) {
            return null;
        }

        @Override
        public void sendError(int sc) throws IOException {
            status = sc;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            status = sc;
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            // dummy
        }

        @Override
        public void setDateHeader(String name, long date) {
            // dummy
        }

        @Override
        public void setHeader(String name, String value) {
            // dummy
        }

        @Override
        public void setIntHeader(String name, int value) {
            // dummy
        }

        @Override
        public void setStatus(int sc, String sm) {
            status = sc;
        }
    }


    private MessageContext messageContext = new MessageContext() {

        private MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        @Override
        public Collection<Object> values() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
            // dummy
        }

        @Override
        public Object put(String key, Object value) {
            return null;
        }

        @Override
        public Set<String> keySet() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Object get(Object key) {
            if (MessageContext.SERVLET_RESPONSE.equals(key)) {
                return mockHttpServletResponse;
            }
            return null;
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return null;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public void clear() {
            // dummy
        }

        @Override
        public void setScope(String name, Scope scope) {
            // dummy
        }

        @Override
        public Scope getScope(String name) {
            return null;
        }
    };

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public MessageContext getMessageContext() {
        return messageContext;
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
        return null;
    }

    @Override
    public EndpointReference getEndpointReference(Element... referenceParameters) {
        return null;
    }

}
