package com.oneandone.ejbcdiunit.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.jboss.weld.context.http.Http;

import com.oneandone.cdi.weldstarter.WeldSetupClass;

/**
 * Decides which version of EjbCdiUnitInitialListener to use, based on the Weld version.
 * @author Sean Flanigan
 */
@ApplicationScoped
public class EjbCdiUnitInitialListenerProducer
{
    private static final String handlerClassName;


    static {
        // workaround for WELD-1269/WELD-2305 changes
        if (WeldSetupClass.isWeld3()) {
            handlerClassName = "org.jboss.weld.module.web.servlet.WeldInitialListener";
        } else {
            handlerClassName = "org.jboss.weld.servlet.WeldListener";
        }
    }

    @Produces
    @Default
    @Http
    EjbCdiUnitInitialListener produce() throws Exception {
            return (EjbCdiUnitInitialListener)Proxy.newProxyInstance(EjbCdiUnitInitialListener.class.getClassLoader(), new Class[]{EjbCdiUnitInitialListener.class}, new InvocationHandler() {
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
                    throw new RuntimeException("expected to find method in EjbCdiUnitInitialListener");
                }
            });
    }
}
