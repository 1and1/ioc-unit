package com.oneandone.iocunit.resteasy.servlet;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

@ApplicationScoped
public class IocUnitServletContextHolder {

    ServletContext servletContext = null;

    public ServletContext getServletContext() {
        if(servletContext == null) {
            servletContext = new ServletContext() {
                @Override
                public String getContextPath() {
                    return null;
                }

                @Override
                public ServletContext getContext(final String uripath) {
                    return null;
                }

                @Override
                public int getMajorVersion() {
                    return 0;
                }

                @Override
                public int getMinorVersion() {
                    return 0;
                }

                @Override
                public int getEffectiveMajorVersion() {
                    return 0;
                }

                @Override
                public int getEffectiveMinorVersion() {
                    return 0;
                }

                @Override
                public String getMimeType(final String file) {
                    return null;
                }

                @Override
                public Set<String> getResourcePaths(final String path) {
                    return null;
                }

                @Override
                public URL getResource(final String path) throws MalformedURLException {
                    return null;
                }

                @Override
                public InputStream getResourceAsStream(final String path) {
                    return null;
                }

                @Override
                public RequestDispatcher getRequestDispatcher(final String path) {
                    return null;
                }

                @Override
                public RequestDispatcher getNamedDispatcher(final String name) {
                    return null;
                }

                @Override
                public void log(final String msg) {

                }

                @Override
                public void log(final String message, final Throwable throwable) {

                }

                @Override
                public String getRealPath(final String path) {
                    return null;
                }

                @Override
                public String getServerInfo() {
                    return null;
                }

                @Override
                public String getInitParameter(final String name) {
                    return null;
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return null;
                }

                @Override
                public boolean setInitParameter(final String name, final String value) {
                    return false;
                }

                @Override
                public Object getAttribute(final String name) {
                    return null;
                }

                @Override
                public Enumeration<String> getAttributeNames() {
                    return null;
                }

                @Override
                public void setAttribute(final String name, final Object object) {

                }

                @Override
                public void removeAttribute(final String name) {

                }

                @Override
                public String getServletContextName() {
                    return null;
                }

                @Override
                public ServletRegistration.Dynamic addServlet(final String servletName, final String className) {
                    return null;
                }

                @Override
                public ServletRegistration.Dynamic addServlet(final String servletName, final Servlet servlet) {
                    return null;
                }

                @Override
                public ServletRegistration.Dynamic addServlet(final String servletName, final Class<? extends Servlet> servletClass) {
                    return null;
                }

                @Override
                public ServletRegistration.Dynamic addJspFile(final String servletName, final String jspFile) {
                    return null;
                }

                @Override
                public <T extends Servlet> T createServlet(final Class<T> clazz) throws ServletException {
                    return null;
                }

                @Override
                public ServletRegistration getServletRegistration(final String servletName) {
                    return null;
                }

                @Override
                public Map<String, ? extends ServletRegistration> getServletRegistrations() {
                    return null;
                }

                @Override
                public FilterRegistration.Dynamic addFilter(final String filterName, final String className) {
                    return null;
                }

                @Override
                public FilterRegistration.Dynamic addFilter(final String filterName, final Filter filter) {
                    return null;
                }

                @Override
                public FilterRegistration.Dynamic addFilter(final String filterName, final Class<? extends Filter> filterClass) {
                    return null;
                }

                @Override
                public <T extends Filter> T createFilter(final Class<T> clazz) throws ServletException {
                    return null;
                }

                @Override
                public FilterRegistration getFilterRegistration(final String filterName) {
                    return null;
                }

                @Override
                public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
                    return null;
                }

                @Override
                public SessionCookieConfig getSessionCookieConfig() {
                    return null;
                }

                @Override
                public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes) {

                }

                @Override
                public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
                    return null;
                }

                @Override
                public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
                    return null;
                }

                @Override
                public void addListener(final String className) {

                }

                @Override
                public <T extends EventListener> void addListener(final T t) {

                }

                @Override
                public void addListener(final Class<? extends EventListener> listenerClass) {

                }

                @Override
                public <T extends EventListener> T createListener(final Class<T> clazz) throws ServletException {
                    return null;
                }

                @Override
                public JspConfigDescriptor getJspConfigDescriptor() {
                    return null;
                }

                @Override
                public ClassLoader getClassLoader() {
                    return null;
                }

                @Override
                public void declareRoles(final String... roleNames) {

                }

                @Override
                public String getVirtualServerName() {
                    return null;
                }

                @Override
                public int getSessionTimeout() {
                    return 0;
                }

                @Override
                public void setSessionTimeout(final int sessionTimeout) {

                }

                @Override
                public String getRequestCharacterEncoding() {
                    return null;
                }

                @Override
                public void setRequestCharacterEncoding(final String encoding) {

                }

                @Override
                public String getResponseCharacterEncoding() {
                    return null;
                }

                @Override
                public void setResponseCharacterEncoding(final String encoding) {

                }
            };
        }
        return servletContext;
    }

    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
