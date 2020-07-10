package com.oneandone.iocunit.analyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class ConfigStatics {

    static Logger logger = LoggerFactory.getLogger(ConfigStatics.class);

    static boolean isInterceptingBean(Class<?> c) {
        if(c.getAnnotation(Interceptor.class) != null || c.getAnnotation(Decorator.class) != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean mightSignCandidate(Class<?> c) {
        return c.isAnnotation() || c.isInterface() || Modifier.isAbstract(c.getModifiers());
    }

    public static boolean isInterceptable(Class<?> c) {
        if(c.equals(Object.class)) {
            return true;
        }
        if(mightBeBean(c)) {
            for (Method m : c.getDeclaredMethods()) {
                if(Modifier.isFinal(m.getModifiers())) {
                    return false;
                }
            }
            return isInterceptable(c.getSuperclass());
        }
        return false;
    }

    public static boolean mightBeBean(Class<?> c) {

        try {
            if(c.isInterface() || c.isPrimitive() || c.isEnum() || c.isLocalClass() || Modifier.isAbstract(c.getModifiers())
               || c.isAnonymousClass() || c.isLocalClass() || c.isAnnotation()
               || (c.getEnclosingClass() != null && !Modifier.isStatic(c.getModifiers()))
               || String.class.isAssignableFrom(c)) {
                return false;
            }
            final Constructor<?>[] declaredConstructors = c.getDeclaredConstructors();
            if(declaredConstructors.length == 0) {
                return false;
            }
            boolean constructorOk = false;
            for (Constructor constructor : declaredConstructors) {
                if(constructor.getParameters().length == 0) {
                    constructorOk = true;
                }
                else {
                    if(constructor.getAnnotation(Inject.class) != null) {
                        constructorOk = true;
                    }
                }
            }
            if(!constructorOk) {
                return false;
            }
            if(isExtension(c)) {
                return false;
            }
        } catch (NoClassDefFoundError | IncompatibleClassChangeError e) {
            logger.warn("NoClassDefFoundError analyzing {}", c.getName());
            return false;
        }
        return true;
    }

    public static boolean isExtension(final Class<?> c) {
        return (Extension.class.isAssignableFrom(c));
    }

    public static boolean isParameterizedType(Type t) {
        if(t instanceof ParameterizedType) {
            return true;
        }
        else {
            if(t == null || t.equals(Object.class)) {
                return false;
            }
            else {
                if(t instanceof Class) {
                    if(((Class) t).getTypeParameters().length > 0) {
                        return true;
                    }
                    return isParameterizedType(((Class) t).getGenericSuperclass());
                }
                else {
                    return false;
                }
            }
        }
    }


    interface ClassHandler {
        void handle(Class<?> c);
    }


    protected static void doInClassAndSuperClasses(final Class<?> c, final ClassHandler classHandler) {
        if(c == null || c.equals(Object.class)) {
            return;
        }
        else {
            classHandler.handle(c);
            doInClassAndSuperClasses(c.getSuperclass(), classHandler);
            return;
        }
    }

    static Class<?>[] setToArray(final Set<Class<?>> setParam) {
        return setParam.toArray(new Class<?>[setParam.size()]);
    }

}
