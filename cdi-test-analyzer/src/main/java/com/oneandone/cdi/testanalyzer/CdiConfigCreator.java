package com.oneandone.cdi.testanalyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.testanalyzer.extensions.TestScopeExtension;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * Starting with InitialConfiguraton analyzes the Class-Structure together with Annotations, to create a minimal startable Weld-SE-Configration.
 */
public class CdiConfigCreator {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private LeveledBuilder builder;
    private List<ProblemRecord> problems = new ArrayList<>();

    private static boolean isInterceptingBean(Class<?> c) {
        if (c.getAnnotation(Interceptor.class) != null || c.getAnnotation(Decorator.class) != null) {
            return true;
        } else
            return false;
    }

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
        if (isExtension(c))
            return false;
        return true;
    }

    public static boolean isExtension(final Class<?> c) {
        return (Extension.class.isAssignableFrom(c));
    }

    public Collection<Class<? extends Extension>> getExtensions() {
        return builder.extensionClasses;
    }

    public Collection<Extension> getExtensionÓbjects() {
        return builder.extensionObjects;
    }

    public Set<Class<?>> getEnabledAlternatives() {
        return builder.enabledAlternatives;
    }

    public Set<Class<?>> getEnabledAlternativeStereotypes() {
        return builder.foundAlternativeStereotypes;
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

    public void create(InitialConfiguration cfg) throws MalformedURLException {
        this.builder = new LeveledBuilder(cfg, new TesterExtensionsConfigsFinder());
        if (cfg.testClass != null && cfg.testClass.getAnnotation(ApplicationScoped.class) == null) {
            builder.extensionObjects.add(new TestScopeExtension(cfg.testClass));
        }
        List<Class<?>> currentToBeEvaluated = builder.extractToBeEvaluatedClasses();

        while (true) {
            // Evaluate classes concerning Annotations, injects, producers,...
            evaluateFoundClasses(currentToBeEvaluated);

            // Injects found should be matched to find out if a search in available classes
            // is necessary

            InjectsMatcher injectsMatcher = new InjectsMatcher(builder);
            injectsMatcher.match();

            currentToBeEvaluated = injectsMatcher.evaluateMatches(problems);

            if (currentToBeEvaluated.size() == 0) {
                if (builder.injections.size() > 0) {
                    // there was no producer left to solve injects.
                    // so search in available classes for producers
                    LeveledBuilder producerBuilder = builder.producerCandidates();
                    InjectsMatcher injectsToProducesMatcher = new InjectsMatcher(producerBuilder);
                    for (QualifiedType inject : builder.injections) {
                        injectsToProducesMatcher.matchInject(inject);
                    }
                    currentToBeEvaluated = injectsToProducesMatcher.evaluateMatches(problems);
                    if (currentToBeEvaluated.size() == 0) {
                        // In available classes nothing could be found to produce for the injects
                        // have to stop algrithm.
                        log.error("New to be started == 0 but");
                        for (QualifiedType q : builder.injections) {
                            log.error("Not resolved: {}", q);
                        }
                        break;
                        // throw new RuntimeException("no producer found");
                    }
                    // new classes are necessary, so repeat cycle.
                } else
                    break;
            } else {
                // injects can be solved by new found available classes.
                // did not need to look inside the classes to search for producers.
            }

            assert (currentToBeEvaluated.size() > 0);
        }

        new InjectsMatcher(builder).matchHandledInject(builder.beansToBeStarted);
    }

    protected void evaluateFoundClasses(List<Class<?>> currentToBeEvaluated) throws MalformedURLException {
        // Further initialize configuration data by looking at initial classes and their annotations.
        // TestClasses and SuTClasses are to be created, if not replaced by newAlternatives.


        while (currentToBeEvaluated.size() > 0) {
            List<Class<?>> l = new ArrayList<>();
            l.addAll(currentToBeEvaluated);
            // l.sort((o1, o2) -> o1.getName().compareTo(o2.getName())); -- keep sequence
            for (Class<?> c : l) {
                log.info("evaluating {}", c);
                if (builder.excludedClasses.contains(c)) {
                    log.info("Excluded {}", c.getName());
                } else if (isInterceptingBean(c)) {
                    // not available for injections and no producer fields!!
                    builder.tobeStarted(c)
                            .innerClasses(c)
                            .injects(c)
                            .elseClass(c);
                    if (builder.isTestClass(c)) {
                        builder.testClassAnnotation(c)
                                .sutClassAnnotation(c)
                                .classpathsAnnotations(c)
                                .packagesAnnotations(c)
                                .customAnnotations(c)
                                .extraAnnotations(c)
                                .excludes(c);
                    }
                } else if (mightBeBean(c)) {
                    builder.tobeStarted(c)
                            .available(c)
                            .innerClasses(c)
                            .injects(c)
                            .producerFields(c)
                            .producerMethods(c);
                    if (builder.isTestClass(c)) {
                        builder.testClassAnnotation(c)
                                .sutClassAnnotation(c)
                                .classpathsAnnotations(c)
                                .packagesAnnotations(c)
                                .enabledAlternatives(c)
                                .customAnnotations(c)
                                .extraAnnotations(c)
                                .excludes(c);
                    }
                } else {
                    builder.elseClass(c);
                }
            }
            currentToBeEvaluated = builder.extractToBeEvaluatedClasses();
        }
    }



    private Collection<Extension> findExtensions() {
        List<Extension> result = new ArrayList<>();
        for (TestExtensionService testExtensionService : builder.testerExtensionsConfigsFinder.testExtensionServices) {
            result.addAll(testExtensionService.getExtensions());
        }
        return result;
    }

    public WeldSetupClass buildWeldSetup(Method method) {
        WeldSetupClass weldSetup = new WeldSetupClass();
        weldSetup.setBeanClasses(toBeStarted());
        weldSetup.setAlternativeClasses(getEnabledAlternatives());
        weldSetup.setEnabledAlternativeStereotypes(getEnabledAlternativeStereotypes());
        weldSetup.setEnabledDecorators(getDecorators());
        weldSetup.setEnabledInterceptors(getInterceptors());
        handleWeldExtensions(method, weldSetup);
        for (Extension e : findExtensions()) {
            weldSetup.addExtensionObject(e);
        }
        return weldSetup;
    }

    private void handleWeldExtensions(final Method method, final WeldSetupClass weldSetup) {
        try {
            for (Class<? extends Extension> extensionClass : getExtensions()) {
                if (extensionClass.getName().contains(".ProducerConfigExtension")) {
                    Constructor<? extends Extension> constructor =
                            extensionClass.getConstructor(Method.class);
                    Extension producerConfig = constructor.newInstance(method);
                    weldSetup.addExtensionObject(producerConfig);
                } else {
                    weldSetup.addExtensionObject(extensionClass.newInstance());
                }
            }
            for (Extension e : getExtensionÓbjects()) {
                Class<? extends Extension> extensionClass = e.getClass();
                final Constructor<?>[] declaredConstructors = extensionClass.getDeclaredConstructors();
                if (declaredConstructors.length == 1 && declaredConstructors[0].getParameters().length == 0) {
                    weldSetup.addExtensionObject(extensionClass.newInstance());
                } else {
                    weldSetup.addExtensionObject(e);
                }
            }
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }


    private Set<Class<?>> toBeStarted() {
        Set<Class<?>> result = new HashSet<>();
        result.addAll(builder.beansToBeStarted);
        result.removeAll(builder.excludedClasses);
        return result;
    }


    private List<Class<?>> getDecorators() {
        return builder.decorators;
    }

    private List<Class<?>> getInterceptors() {
        return builder.interceptors;
    }

}
