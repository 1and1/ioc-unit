package com.oneandone.iocunit.resteasy;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitMockDispatcherFactory;
import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class DispatcherDelegate implements IocUnitResteasyDispatcher, AutoCloseable {

    @Inject
    private JaxRSRestEasyTestExtension jaxRsTestExtension;

    @Inject
    private BeanManager beanManager;

    private CreationalContexts creationalContexts;

    Logger logger = LoggerFactory.getLogger("RestEasy MockDispatcher Delegate");
    boolean setupDone = false;

    static ThreadLocal<SecurityContext> securityContextThreadLocal = new ThreadLocal<>();
    static ThreadLocal<ServletContext> servletContextThreadLocal = new ThreadLocal<>();

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
        delegate = IocUnitMockDispatcherFactory.createDispatcher();
        addAnnotationDefinedJaxRSClasses();
        creationalContexts = new CreationalContexts(beanManager);
        for (Class<?> clazz : jaxRsTestExtension.getResourceClasses()) {
            logger.info("Creating restresource {}", clazz.getName());
            try {
                Object res = creationalContexts.create(clazz, ApplicationScoped.class);
                delegate.getRegistry().addSingletonResource(res);
            } catch (Throwable thw) {
                logger.debug(thw.getMessage(), thw);
            }
        }

        ResteasyProviderFactory provfactory = delegate.getProviderFactory();
        for (Class<?> clazz : jaxRsTestExtension.getProviders()) {
            logger.info("Creating rest-provider {}", clazz.getName());
            Object res = creationalContexts.create(clazz, ApplicationScoped.class);
            IocUnitMockDispatcherFactory.register(res);
        }
        handleContext(ServletContext.class, servletContextThreadLocal);
        handleContext(SecurityContext.class, securityContextThreadLocal);
        checkJackson(provfactory);
    }

    private <T> void handleContext(Class<T> interfaceClass, ThreadLocal<T> threadLocalContainer) {
        try {
            T bean = (T) creationalContexts.create(interfaceClass, ApplicationScoped.class);
            IocUnitMockDispatcherFactory.getContextDataMap().put(interfaceClass, bean);
            threadLocalContainer.set(bean);
        } catch (Exception e) {
            if(e.getClass().getName().contains("AmbiguousResolutionException")) {
                throw new RuntimeException(e);
            }
            else {
                logger.info("No Test " + interfaceClass.getName() + " found");
            }
        }
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

    IocUnitResteasyDispatcher delegate;

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
            if(creationalContexts != null) {
                creationalContexts.close();
            }
            creationalContexts = null;
            delegate = null;
            jaxRsTestExtension = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static SecurityContext getSecurityContext() {
        return securityContextThreadLocal.get();
    }

    public static ServletContext getServletContext() {
        return servletContextThreadLocal.get();
    }

}
