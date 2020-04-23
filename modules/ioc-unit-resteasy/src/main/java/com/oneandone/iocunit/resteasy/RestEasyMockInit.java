package com.oneandone.iocunit.resteasy;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.resteasy.core.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class RestEasyMockInit {

    Logger logger = LoggerFactory.getLogger("RestEasyMockDispatcherFactory init");

    Dispatcher dispatcher;

    @Inject
    JaxRsRestEasyTestExtension jaxRsRestEasyTestExtension;

    @Produces
    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    @PostConstruct
    public void setUp() {
        dispatcher = new DispatcherDelegate(jaxRsRestEasyTestExtension);

        try {
            Class initClass = Class.forName("com.oneandone.iocunit.resteasy.restassured.Initializer");
            Method initmethod = initClass.getMethod("init", Dispatcher.class);
            initmethod.invoke(null, dispatcher);
        } catch (Throwable e) {
            if(e.getClass().equals(NoClassDefFoundError.class) && e.getMessage().equals("io/restassured/config/HttpClientConfig$HttpClientFactory")) {
                ;
            }
            else {
                logger.warn("Restassured not initialized", e);
            }
        }

    }
}
