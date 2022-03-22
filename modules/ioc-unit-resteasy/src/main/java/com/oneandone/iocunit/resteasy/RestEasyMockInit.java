package com.oneandone.iocunit.resteasy;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.jboss.resteasy.mock.IocUnitResteasyDispatcher;

import io.restassured.RestAssured;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class RestEasyMockInit {

    Logger logger = LoggerFactory.getLogger("RestEasyMockDispatcherFactory init");

    DispatcherDelegate dispatcher;

    @Inject
    JaxRSRestEasyTestExtension jaxRsRestEasyTestExtension;

    @Produces
    public IocUnitResteasyDispatcher getDispatcher() {
        return dispatcher;
    }

    Closeable factory = null;

    @PostConstruct
    public void setUp() {
        dispatcher = new DispatcherDelegate(jaxRsRestEasyTestExtension);

        try {
            Class initClass = Class.forName("com.oneandone.iocunit.resteasy.restassured.Initializer");
            Method initmethod = initClass.getMethod("init", DispatcherDelegate.class);
            factory = (Closeable)initmethod.invoke(null, dispatcher);
        } catch (Throwable e) {
            handleNotDefinedError(e);
        }
    }

    @PreDestroy
    public void preDestroy() {
            if (factory != null) {
                try {
                    factory.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                RestAssured.config = null;
            }
            if (dispatcher != null)
                dispatcher.close();
    }

    private void handleNotDefinedError(final Throwable e) {
        if(e.getClass().equals(NoClassDefFoundError.class) && e.getMessage().equals("io/restassured/config/HttpClientConfig$HttpClientFactory")) {
            ;
        }
        else {
            logger.warn("Restassured not initialized", e);
        }
    }
}
