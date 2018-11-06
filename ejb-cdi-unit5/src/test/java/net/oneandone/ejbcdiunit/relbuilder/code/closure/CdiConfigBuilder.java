package net.oneandone.ejbcdiunit.relbuilder.code.closure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
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

        Map<QualifiedType, Set<QualifiedType>> matching = new HashMap<>();
        Map<QualifiedType, Set<QualifiedType>> ambiguus = new HashMap<>();
        Set<QualifiedType> empty = new HashSet<>();

        for (QualifiedType inject : builder.injections) {
            Set<QualifiedType> foundProducers = new HashSet<>();
            Set<QualifiedType> producers = builder.producerMap.get(inject.getRawtype());
            for (QualifiedType q : producers) {
                if (TypeUtils.isAssignable(q.getType(), inject.getType())) {
                    foundProducers.add(q);
                }
            }
            Set<Class<?>> foundClasses = builder.classMap.get(inject.getRawtype());
            for (Class c : foundClasses) {
                foundProducers.add(new QualifiedType(c));
            }

            // check types and qualifiers of results
            matching.put(inject, new HashSet<>());
            for (QualifiedType qp : foundProducers) {
                if (TypeUtils.isAssignable(qp.getType(), inject.getType())) {
                    if (qualifiersMatch(inject, qp))
                        matching.get(inject).add(qp);
                }
            }
            if (matching.get(inject).size() == 0) {
                empty.add(inject);
            } else if (matching.get(inject).size() > 1) {
                ambiguus.put(inject, matching.get(inject));
                matching.remove(inject);
            }
        }

        for (QualifiedType inject : empty) {
            // search for producers and inner classes
        }

        for (QualifiedType inject : matching.keySet()) {
            // check available, if yes then add for next iteration
            // check else: available
        }

        for (QualifiedType inject : ambiguus.keySet()) {
            // prefer testclasses
            // check else: available
        }
        // check priority by looking at
        // testclasses
        // classesToBeBuilt

        // availableClasses
        // producers of available classes


    }

    private Boolean qualifiersMatch(final QualifiedType qi, final QualifiedType qp) {
        if (qi.getQualifiers().isEmpty()) {
            if (hasDefault(qp.getQualifiers()) || hasAny(qp.getQualifiers())) {
                return true;
            } else
                return false;
        }
        if (qp.getQualifiers().isEmpty()) {
            if (qi.getQualifiers().size() <= 1 && hasDefault(qi.getQualifiers()))
                return true;
            else
                return false;
        }
        for (Annotation ai : qi.getQualifiers()) {
            if (ai.annotationType().getName().equals(Default.class.getName())) {
                if (!hasDefault(qp.getQualifiers())) {
                    return false;
                }
            } else {
                boolean found = false;
                for (Annotation ap : qp.getQualifiers()) {
                    if (ap.equals(ai)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasDefault(final Set<Annotation> qualifiers) {
        if (qualifiers.isEmpty())
            return true;
        for (Annotation a : qualifiers) {
            if (a.annotationType().getName().equals(Default.class.getName()))
                return true;
        }
        return false;
    }

    private boolean hasAny(final Set<Annotation> qualifiers) {
        for (Annotation a : qualifiers) {
            if (a.annotationType().getName().equals(Any.class.getName()))
                return true;
        }
        return false;
    }

    public void initialize(InitialConfiguration cfg) throws MalformedURLException {

        Set<Class<?>> tmp = new HashSet<>();
        tmp.add(cfg.testClass);
        tmp.addAll(cfg.initialClasses);
        evaluateLevel(tmp);

    }
}
