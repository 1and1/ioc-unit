package com.oneandone.iocunit.resteasy;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;

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

/**
 * @author aschoerk
 */
public class DispatcherDelegate implements Dispatcher {


    public DispatcherDelegate(final JaxRsRestEasyTestExtension jaxRsTestExtension) {
        this.jaxRsTestExtension = jaxRsTestExtension;
    }

    JaxRsRestEasyTestExtension jaxRsTestExtension;
    private CreationalContexts creationalContexts;

    Logger logger = LoggerFactory.getLogger("RestEasy MockDispatcher Delegate");
    boolean setupDone = false;

    public void setUp() {
        if(setupDone) {
            return;
        }
        setupDone = true;
        delegate = MockDispatcherFactory.createDispatcher();
        try {
            creationalContexts = new CreationalContexts();
            for (Class<?> clazz : jaxRsTestExtension.getResourceClasses()) {
                logger.info("Creating restresource {}", clazz.getName());
                Object res = creationalContexts.create(clazz, ApplicationScoped.class);
                delegate.getRegistry().addSingletonResource(res);
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

        checkJackson(provfactory);
    }

    private void checkJackson(final ResteasyProviderFactory provfactory) {

        boolean jackson1Found = false;
        boolean jackson2Found = false;
        for (Class c: provfactory.getClasses()) {
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
}
