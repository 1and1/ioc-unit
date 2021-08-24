package com.oneandone.iocunit.resteasy;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;

/**
 * @author aschoerk
 */
public class DispatcherDelegate implements Dispatcher, AutoCloseable {


    public DispatcherDelegate(final JaxRSRestEasyTestExtension jaxRsTestExtension) {
        this.jaxRsTestExtension = jaxRsTestExtension;
    }

    private JaxRSRestEasyTestExtension jaxRsTestExtension;
    private CreationalContexts creationalContexts;

    Logger logger = LoggerFactory.getLogger("RestEasy MockDispatcher Delegate");
    boolean setupDone = false;

    static ThreadLocal<Object> securityContextThreadLocal = new ThreadLocal<>();

    private void addAnnotationDefinedJaxRSClasses() {
        Boolean onlyAnnotationDefined = RestEasyTestExtensionServices.onlyAnnotationDefined.get();
        if(onlyAnnotationDefined != null && onlyAnnotationDefined) {
            jaxRsTestExtension.getProviders().clear();
            jaxRsTestExtension.getResourceClasses().clear();
        }
        for (Class c : RestEasyTestExtensionServices.perAnnotationDefinedJaxRSClasses.get()) {
            if(JaxRSRestEasyTestExtension.annotationPresent(c, Provider.class)) {
                jaxRsTestExtension.getProviders().add(c);
            }
            else {
                jaxRsTestExtension.getResourceClasses().add(c);
            }
        }
    }


    public void setUp() {
        if(setupDone) {
            return;
        }
        setupDone = true;
        delegate = MockDispatcherFactory.createDispatcher();
        addAnnotationDefinedJaxRSClasses();
        try {
            creationalContexts = new CreationalContexts();
            for (Class<?> clazz : jaxRsTestExtension.getResourceClasses()) {
                logger.info("Creating restresource {}", clazz.getName());
                try {
                    Object res = creationalContexts.create(clazz, ApplicationScoped.class);
                    delegate.getRegistry().addSingletonResource(res);
                } catch (Throwable thw) {
                    logger.debug(thw.getMessage(), thw);
                }
            }

        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        ResteasyProviderFactory provfactory = delegate.getProviderFactory();
        for (Class<?> clazz : jaxRsTestExtension.getProviders()) {
            logger.info("Creating rest-provider {}", clazz.getName());
            Object res = creationalContexts.create(clazz, ApplicationScoped.class);
            provfactory.register(res);
        }
        try {
            Object securityContext = creationalContexts.create(SecurityContext.class, ApplicationScoped.class);
            ResteasyProviderFactory.getContextDataMap().put(SecurityContext.class, securityContext);
            securityContextThreadLocal.set(securityContext);
        } catch (Exception e) {
            if(e.getClass().getName().contains("AmbiguousResolutionException")) {
                throw new RuntimeException(e);
            }
            else {
                logger.info("No Test SecurityContext found");
            }
        }

        checkJackson(provfactory);
    }

    private void checkJackson(final ResteasyProviderFactory provfactory) {

        try {
            boolean jackson1Found = false;
            boolean jackson2Found = false;
            for (Class c : provfactory.getClasses()) {
                if(c.getName().equals("org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider")) {
                    jackson2Found = true;
                }
                else if(c.getName().equals("org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider")) {
                    jackson1Found = true;
                }
            }
            if(jackson1Found) {
                logger.info("ResteasyJacksonProvider found");
            }
            if(jackson2Found) {
                logger.info("ResteasyJackson2Provider found");
            }
            if(jackson1Found && jackson2Found) {
                logger.warn("Both ResteasyJacksonProvider and ResteasyJackson2Provider found!");
            }
        } catch (NoSuchMethodError e) {
            if (!WeldSetupClass.isWeld1())
                throw e;
        }

    }

    Dispatcher delegate;

    @Override
    public ResteasyProviderFactory getProviderFactory() {
        setUp();
        return delegate.getProviderFactory();
    }

    @Override
    public Registry getRegistry() {
        setUp();
        return delegate.getRegistry();
    }

    @Override
    public void invoke(final HttpRequest in, final HttpResponse response) {
        setUp();
        delegate.invoke(in, response);
    }

    @Override
    public Response internalInvocation(final HttpRequest request, final HttpResponse response, final Object entity) {
        setUp();
        return delegate.internalInvocation(request, response, entity);
    }

    @Override
    public void addHttpPreprocessor(final HttpRequestPreprocessor httpPreprocessor) {
        setUp();
        delegate.addHttpPreprocessor(httpPreprocessor);
    }

    @Override
    public Map<Class, Object> getDefaultContextObjects() {
        setUp();
        return delegate.getDefaultContextObjects();
    }

    @Override
    public void close() {
        try {
            if (creationalContexts != null)
                creationalContexts.close();
            creationalContexts = null;
            delegate = null;
            jaxRsTestExtension = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
