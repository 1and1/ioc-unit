package com.oneandone.iocunit.resteasy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class RestEasyMockInit {

    private Dispatcher dispatcher;
    private CreationalContexts creationalContexts;

    @Inject
    JaxRsRestEasyTestExtension jaxRsTestExtension;

    @Produces
    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    @PostConstruct
    public void setUp() {
        dispatcher = MockDispatcherFactory.createDispatcher();
        try {
            creationalContexts = new CreationalContexts();
            for (Class<?> clazz: jaxRsTestExtension.getResourceClasses()) {
                Object res = creationalContexts.create(clazz, ApplicationScoped.class);
                dispatcher.getRegistry().addSingletonResource(res);
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        ResteasyProviderFactory provfactory = dispatcher.getProviderFactory();
        for (Class<?> clazz: jaxRsTestExtension.getExceptionMappers()) {
            Type[] genInterfaces = clazz.getGenericInterfaces();
            for (Type t: genInterfaces) {
                if (t instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) t;
                    if (pt.getRawType().equals(ExceptionMapper.class)) {
                        provfactory.getExceptionMappers().put(
                                (Class)(pt.getActualTypeArguments()[0]),
                                (ExceptionMapper) provfactory.createProviderInstance(clazz));
                    }
                }
            }
        }
    }
}
