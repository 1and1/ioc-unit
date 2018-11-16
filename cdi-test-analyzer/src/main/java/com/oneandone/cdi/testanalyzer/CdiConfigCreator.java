package com.oneandone.cdi.testanalyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

public class CdiConfigCreator {

    private LeveledBuilder builder;
    public static boolean mightBeBean(Class<?> c) {
        if (c.isInterface() || c.isPrimitive() || c.isLocalClass()
                || c.isAnonymousClass() || c.isLocalClass() || c.isAnnotation()
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

    public Collection<Class<? extends Extension>> getExtensions() {
        return builder.extensions;
    }

    public Set<Class<?>> getEnabledAlternatives() {
        return builder.data.enabledAlternatives;
    }

    public Set<Class<?>> getEnabledAlternativeStereotypes() {
        return builder.data.foundAlternativeStereotypes;
    }

    public static class ProblemRecord {
        private String msg;
        private Collection<QualifiedType> qualifiedTypes;
        Collection<Class<?>>[] classes;
        QualifiedType inject;

        public ProblemRecord(String msg, QualifiedType inject, Collection<Class<?>>... classes) {
            this.classes = classes;
            this.inject = inject;
            this.msg = msg;
        }

        public ProblemRecord(String msg, QualifiedType inject, Collection<QualifiedType> qualifiedTypes) {
            this.qualifiedTypes = qualifiedTypes;
            this.inject = inject;
            this.msg = msg;
        }
    }

    List<ProblemRecord> problems = new ArrayList<>();


    void create(Set<Class<?>> beansToBeEvaluated, InitialConfiguration cfg) throws MalformedURLException {
        Set<Class<?>> currentToBeEvaluated = new HashSet<>(beansToBeEvaluated);
        this.builder = new LeveledBuilder(cfg);
        // handle initial classes as testclasses
        for (Class<?> c : currentToBeEvaluated) {
            if (mightBeBean(c)) {
                builder.testClass(c);
            }  else {
                builder.elseClass(c);
            }
        }

        // Initialize configuration data by looking at initial classes and their annotations.
        // TestClasses and SuTClasses are to be created, if not replaced by alternatives.
        while (currentToBeEvaluated.size() > 0) {
            for (Class<?> c : currentToBeEvaluated) {
                if (mightBeBean(c)) {
                    builder.tobeStarted(c)
                            .setAvailable(c)
                            .innerClasses(c)
                            .injects(c)
                            .producerFields(c)
                            .producerMethods(c)
                            .testClassAnnotation(c)
                            .sutClassAnnotation(c)
                            .sutClasspathsAnnotation(c)
                            .sutPackagesAnnotation(c)
                            .enabledAlternatives(c);

                } else {
                    builder.elseClass(c);
                }
            }
            builder.moveToBeEvaluatedTo(currentToBeEvaluated);
        }


        while (true) {
            InjectsMatcher injectsMatcher = new InjectsMatcher(builder);
            injectsMatcher.match();

            Set<Class<?>> newToBeStarted = injectsMatcher.evaluateMatches(problems);

            if (newToBeStarted.size() == 0) {
                if (builder.injections.size() > 0) {
                    LeveledBuilder producerBuilder = builder.producerCandidates();
                    InjectsMatcher injectsToProducesMatcher = new InjectsMatcher(producerBuilder);
                    for (QualifiedType inject : builder.injections) {
                        injectsToProducesMatcher.matchInject(inject);
                    }
                    newToBeStarted = injectsToProducesMatcher.evaluateMatches(problems);
                    if (newToBeStarted.size() == 0) {
                        throw new RuntimeException("no producer found");
                    }
                } else
                    return;
            }
            assert (newToBeStarted.size() > 0);
            for (Class<?> c : newToBeStarted) {
                builder.tobeStarted(c)
                        .setAvailable(c)
                        .innerClasses(c)
                        .injects(c)
                        .producerFields(c)
                        .producerMethods(c)
                        .testClassAnnotation(c)
                        .sutClassAnnotation(c)
                        .sutClasspathsAnnotation(c)
                        .sutPackagesAnnotation(c)
                        .enabledAlternatives(c);
            }
        }

    }

    public Set<Class<?>> toBeStarted() {
        return this.builder.data.beansToBeStarted;
    }


    public void initialize(InitialConfiguration cfg) throws MalformedURLException {

        Set<Class<?>> tmp = new HashSet<>();
        if (cfg.testClass != null)
            tmp.add(cfg.testClass);
        tmp.addAll(cfg.initialClasses);
        tmp.addAll(cfg.enabledAlternatives);
        create(tmp, cfg);

    }
}
