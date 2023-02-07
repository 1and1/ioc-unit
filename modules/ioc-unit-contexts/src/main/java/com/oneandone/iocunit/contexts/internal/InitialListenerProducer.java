package com.oneandone.iocunit.contexts.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;

import org.jboss.weld.context.http.Http;

import com.oneandone.cdi.weldstarter.WeldSetupClass;

/**
 * Decides which version of EjbCdiUnitInitialListener to use, based on the Weld version.
 * @author Sean Flanigan
 */
@ApplicationScoped
public class InitialListenerProducer
{
    private static final String handlerClassName;


    static {
        handlerClassName = "org.jboss.weld.module.web.servlet.WeldInitialListener";
    }

    @Produces
    @Default
    @Http
    InitialListener produce() throws Exception {
        return (InitialListener) Proxy.newProxyInstance(InitialListener.class.getClassLoader(), new Class[] { InitialListener.class },
                new InvocationHandler() {
                Object handler = null;

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (handler == null) {
                        handler = Class.forName(handlerClassName).newInstance();
                    }
                    for (Method m: handler.getClass().getMethods()) {
                       if (m.getName().equals(method.getName())) {
                           return m.invoke(handler, args);
                       }
                    }
                        throw new RuntimeException("expected to find method in InitialListenerProxy");
                }
            });
    }
}
