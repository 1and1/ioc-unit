package net.oneandone.ejbcdiunit.relbuilder.code.closure;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.apache.commons.lang3.reflect.TypeUtils;

public class CdiConfigBuilder {

    public static boolean mightBeBean(Class<?> c) {
        if (c.isInterface() || c.isPrimitive() || c.isLocalClass()
                || c.isAnonymousClass() || c.isLocalClass()
                || (c.getEnclosingClass() != null && !Modifier.isStatic(c.getModifiers())))
            return false;
        final Constructor<?>[] declaredConstructors = c.getDeclaredConstructors();
        if (declaredConstructors.length == 0)
            return false;
        boolean constructorOk = false;
        for (Constructor constructor : declaredConstructors) {
            if (constructor.getParameters().length == 0) {
                constructorOk = true;
            } else {
                if (constructor.getAnnotation(Inject.class) != null)
                    constructorOk = true;
            }
        }
        if (!constructorOk)
            return false;
        if (c.getAnnotation(Interceptor.class) != null && c.getAnnotation(Decorator.class) != null)
            return false;
        if (isExtension(c))
            return false;
        return true;
    }

    public static boolean isExtension(final Class<?> c) {
        return (Extension.class.isAssignableFrom(c));
    }


    void evaluateLevel(Set<Class<?>> beansToBeEvaluated) throws MalformedURLException {
        Set<Class<?>> currentToBeEvaluated = new HashSet<>(beansToBeEvaluated);
        Builder builder = new Builder();

        while (currentToBeEvaluated.size() > 0) {
            for (Class<?> c : currentToBeEvaluated) {
                if (mightBeBean(c)) {
                    builder.tobeStarted(c)
                            .setAvailable(c)
                            .innerClasses(c)
                            .injects(c)
                            .producerFields(c)
                            .producerMethods(c)
                            .testClasses(c)
                            .sutClasses(c)
                            .sutClasspaths(c)
                            .sutPackages(c)
                            .alternatives(c);

                } else {
                    builder.elseClass(c);
                }
            }
            builder.moveToBeEvaluatedTo(currentToBeEvaluated);
        }
        for (QualifiedType inject : builder.injections) {
            Set<QualifiedType> foundProducers = new HashSet<>();
            Set<QualifiedType> producers = builder.producerMap.get(inject.getRawtype());
            for (QualifiedType q : producers) {
                if (TypeUtils.isAssignable(q.getType(), inject.getType())) {
                    foundProducers.add(q);
                }
            }
            Set<Class<?>> foundClasses = builder.classMap.get(inject.getRawtype());

            // check qualifiers of results

            // check priority by looking at
            // testclasses
            // classesToBeBuilt

            // availableClasses
            // producers of available classes

        }


    }


    public void initialize(InitialConfiguration cfg) {

        Set<Class<?>> tmp = new HashSet<>();
        tmp.add(cfg.testClass);
        tmp.addAll(cfg.initialClasses);
        evaluateLevel(tmp);

    }
}
