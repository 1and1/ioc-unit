package com.oneandone.ejbcdiunit.internal;

import org.jboss.weld.context.http.Http;
import org.jboss.weld.module.web.servlet.WeldInitialListener;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        String tmp;
        try {
            Class c = WeldInitialListener.class;

            tmp = "org.jboss.weld.module.web.servlet.WeldInitialListener";
        } catch (NoClassDefFoundError e) {

            tmp = "org.jboss.weld.servlet.WeldListener";
        }
        handlerClassName = tmp;
        ;
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
