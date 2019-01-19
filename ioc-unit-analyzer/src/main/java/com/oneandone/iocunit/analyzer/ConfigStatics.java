package com.oneandone.iocunit.analyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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

    public static boolean mightBeBean(Class<?> c) {

        try {
            if(c.isInterface() || c.isPrimitive() || c.isLocalClass() || Modifier.isAbstract(c.getModifiers())
               || c.isAnonymousClass() || c.isLocalClass() || c.isAnnotation()
               || (c.getEnclosingClass() != null && !Modifier.isStatic(c.getModifiers()))) {
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
        } catch (NoClassDefFoundError|IncompatibleClassChangeError e) {
            return false;
        }
        return true;
    }

    public static boolean isExtension(final Class<?> c) {
        return (Extension.class.isAssignableFrom(c));
    }

    protected static void doInClassAndSuperClasses(final Class<?> c, final LeveledBuilder.ClassHandler classHandler) {
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
