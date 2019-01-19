package com.oneandone.iocunit.analyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.analyzer.extensions.TestScopeExtension;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * Starting with InitialConfiguraton analyzes the Class-Structure together with Annotations, to create testerExtensionsConfigsFinder minimal startable Weld-SE-Configration.
 */
public class CdiConfigCreator {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private LeveledBuilder builder;
    private List<ProblemRecord> problems = new ArrayList<>();

    public Collection<Class<? extends Extension>> getExtensions() {
        return builder.elseClasses.extensionClasses;
    }

    public Collection<Extension> getExtensionÓbjects() {
        return builder.elseClasses.extensionObjects;
    }

    public Set<Class<?>> getEnabledAlternatives() {
        return builder.enabledAlternatives;
    }

    public Set<Class<?>> getEnabledAlternativeStereotypes() {
        return builder.elseClasses.foundAlternativeStereotypes;
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
            builder.elseClasses.extensionObjects.add(new TestScopeExtension(cfg.testClass));
        }
        List<Class<?>> currentToBeEvaluated = builder.extractToBeEvaluatedClasses();

        while (true) {
            // Evaluate classes concerning Annotations, injects, producers,...
            if (!evaluateFoundClasses(currentToBeEvaluated)) {
                break;
            }

            // Injects found should be matched to find out if testerExtensionsConfigsFinder search in available classes
            // is necessary

            InjectsMatcher injectsMatcher = new InjectsMatcher(builder);
            injectsMatcher.match();

            currentToBeEvaluated = injectsMatcher.evaluateMatches(problems);
            builder.incrementLevel();

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
                    if (currentToBeEvaluated.size() == 0 && builder.injections.size() > 0) {
                        // In available classes nothing could be found to produce for the injects
                        // have to stop algrithm.

                        List<QualifiedType> handled = new ArrayList<>();
                        for (QualifiedType q : builder.injections) {
                            if (!Instance.class.isAssignableFrom(q.getRawtype())
                                    && !InjectionPoint.class.isAssignableFrom(q.getRawtype()))
                                log.error("Not resolved: {}", q);
                            else {
                                handled.add(q);
                            }
                        }
                        builder.injections.removeAll(handled);
                        builder.handledInjections.addAll(handled);
                        break;
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

    protected boolean evaluateFoundClasses(List<Class<?>> currentToBeEvaluated) throws MalformedURLException {
        // Further initialize configuration data by looking at initial classes and their annotations.
        // TestClasses and SuTClasses are to be created, if not replaced by newAlternatives.


        boolean didChanges = false;
        while (currentToBeEvaluated.size() > 0) {
            ArrayList<Class<?>> sortedList = sortByPriority(currentToBeEvaluated);

            for (Class<?> c : sortedList) {
                log.trace("evaluating {}", c);
                if (builder.excludedClasses.contains(c)) {
                    log.info("Excluded {}", c.getName());
                } else {
                    didChanges = true;
                    if(ConfigStatics.isInterceptingBean(c)) {
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
                    }
                    else if(ConfigStatics.mightBeBean(c)) {
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
            }
            currentToBeEvaluated = builder.extractToBeEvaluatedClasses();
        }
        return didChanges;
    }

    private ArrayList<Class<?>> sortByPriority(final List<Class<?>> currentToBeEvaluated) {
        Set<Class<?>> priorityClasses = new HashSet<>();
        ArrayList<Class<?>> sortedList = new ArrayList<>();
        currentToBeEvaluated
                .stream()
                .filter(Class::isAnnotation)
                .forEach(c -> {
                    sortedList.add(c);
                    priorityClasses.add(c);
                });
        currentToBeEvaluated
                .stream()
                .filter(c -> c.getAnnotation(Alternative.class) != null && !priorityClasses.contains(c))
                .forEach(c -> {
                    sortedList.add(c);
                    priorityClasses.add(c);
                });
        priorityClasses.addAll(sortedList);
        currentToBeEvaluated
                .stream()
                .filter(c -> !priorityClasses.contains(c))
                .forEach(c -> sortedList.add(c));
        return sortedList;
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
        if (log.isTraceEnabled()) {
            for (Class<?> i : getDecorators()) {
                log.trace("buildWeldSetup Decorator:   {}", i);
            }
            for (Class<?> i : getInterceptors()) {
                log.trace("buildWeldSetup Interceptor: {}", i);
            }
        }
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
        return builder.elseClasses.decorators;
    }

    private List<Class<?>> getInterceptors() {
        return builder.elseClasses.interceptors;
    }

}
