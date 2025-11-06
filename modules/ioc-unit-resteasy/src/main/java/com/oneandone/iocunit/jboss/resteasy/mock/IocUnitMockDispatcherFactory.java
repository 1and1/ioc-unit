package com.oneandone.iocunit.jboss.resteasy.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class IocUnitMockDispatcherFactory {
    private static Class<?> resteasyContextClass = null;
    private static Method getContextDataMethod = null;
    private static Method clearContextDataMethod = null;
    private static Method getContextDataLevelCount = null;
    private static Method removeContextDataLevel = null;
    private static Method addContextDataLevel = null;

    private static Method registerMethod = null;
    private static Class<?> resteasyContextFactoryClass = null;

    static {
        try {
            resteasyContextFactoryClass = Class.forName("org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl");
            resteasyContextClass = Class.forName("org.jboss.resteasy.core.ResteasyContext");
            getContextDataMethod = resteasyContextClass.getMethod("getContextDataMap");
            clearContextDataMethod = resteasyContextClass.getMethod("clearContextData");
            getContextDataLevelCount = resteasyContextClass.getMethod("getContextDataLevelCount");
            removeContextDataLevel = resteasyContextClass.getMethod("removeContextDataLevel");
            addContextDataLevel = resteasyContextClass.getMethod("addContextDataLevel");
            registerMethod = resteasyContextFactoryClass.getMethod("register", Object.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
        }
    }

    public static boolean isResteasyContextAvailable() {
        return resteasyContextClass != null;
    }

    public static int getContextDataLevelCount() {
        if (getContextDataLevelCount != null) {
            try {
                return (int) getContextDataLevelCount.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException("Resteasy version does not support getContextDataLevelCount");
        }
    }

    public static void removeContextDataLevel() {
        if (removeContextDataLevel != null) {
            try {
                removeContextDataLevel.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException("Resteasy version does not support removeContextDataLevel");
        }
    }

    public static void addContextDataLevel() {
        if (addContextDataLevel != null) {
            try {
                addContextDataLevel.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException("Resteasy version does not support addContextDataLevel");
        }
    }

    public static void register(Object object) {
        if(registerMethod != null) {
            try {
                registerMethod.invoke(ResteasyProviderFactory.getInstance(), object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            ResteasyProviderFactory.getInstance().register(object);
        }
    }

    public static Map<Class<?>, Object> getContextDataMap() {
        if(getContextDataMethod != null) {
            try {
                return (Map<Class<?>, Object>) getContextDataMethod.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            return ResteasyProviderFactory.getContextDataMap();
        }
    }

    public static void clearContextData() {
        if(clearContextDataMethod != null) {
            try {
                clearContextDataMethod.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            ResteasyProviderFactory.clearContextData();
        }
    }

    public static IocUnitResteasyDispatcher createDispatcher() {
        ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
        SynchronousDispatcher dispatcher = new SynchronousDispatcher(providerFactory);
        ResteasyProviderFactory.setInstance(dispatcher.getProviderFactory());
        RegisterBuiltin.register(dispatcher.getProviderFactory());
        return new IocUnitResteasyDispatcher() {
            @Override
            public ResteasyProviderFactory getProviderFactory() {
                return dispatcher.getProviderFactory();
            }

            @Override
            public Registry getRegistry() {
                return dispatcher.getRegistry();
            }

            @Override
            public void invoke(final HttpRequest var1, final HttpResponse var2) {
                dispatcher.invoke(var1, var2);

            }

            @Override
            public Response internalInvocation(final HttpRequest var1, final HttpResponse var2, final Object var3) {
                return dispatcher.internalInvocation(var1, var2, var3);
            }

            @Override
            public void addHttpPreprocessor(final HttpRequestPreprocessor var1) {

                dispatcher.addHttpPreprocessor(var1);
            }

            @Override
            public Map<Class, Object> getDefaultContextObjects() {
                return dispatcher.getDefaultContextObjects();
            }

            @Override
            public void setUp() {

            }
        };
    }

}
