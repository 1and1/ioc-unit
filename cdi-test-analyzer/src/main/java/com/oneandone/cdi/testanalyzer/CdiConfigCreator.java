package com.oneandone.cdi.testanalyzer;

import com.oneandone.cdi.extensions.TestExtensionService;
import com.oneandone.cdi.extensions.TestScopeExtension;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.decorator.Decorator;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Starting with InitialConfiguraton analyzes the Class-Structure together with Annotations, to create a minimal
 * startable Weld-SE-Configration.
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
        this.builder = new LeveledBuilder(cfg);
        if (cfg.testClass != null && cfg.testClass.getAnnotation(ApplicationScoped.class) == null) {
            builder.extensionObjects.add(new TestScopeExtension(cfg.testClass));
        }
        Set<Class<?>> currentToBeEvaluated = builder.extractToBeEvaluatedClasses();



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
                        return;
                        // throw new RuntimeException("no producer found");
                    }
                    // new classes are necessary, so repeat cycle.
                } else
                    return;
            } else {
                // injects can be solved by new found available classes.
                // did not need to look inside the classes to search for producers.
            }

            assert (currentToBeEvaluated.size() > 0);
        }
    }

    protected void evaluateFoundClasses(Set<Class<?>> currentToBeEvaluated) throws MalformedURLException {
        // Further initialize configuration data by looking at initial classes and their annotations.
        // TestClasses and SuTClasses are to be created, if not replaced by alternatives.
        while (currentToBeEvaluated.size() > 0) {
            for (Class<?> c : currentToBeEvaluated) {
                if (isInterceptingBean(c)) {
                    builder.tobeStarted(c)
                            .innerClasses(c)
                            .injects(c)
                            .elseClass(c)
                            .testClassAnnotation(c)
                            .sutClassAnnotation(c)
                            .sutClasspathsAnnotation(c)
                            .sutPackagesAnnotation(c);
                } else if (mightBeBean(c)) {
                    builder.tobeStarted(c)
                            .available(c)
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
            currentToBeEvaluated = builder.extractToBeEvaluatedClasses();
        }
    }


    private Collection<Extension> findExtensions() {
        List<Extension> result = new ArrayList<>();
        ServiceLoader<TestExtensionService> loader = ServiceLoader.load(TestExtensionService.class);
        final Iterator<TestExtensionService> testExtensionServiceIterator = loader.iterator();
        while (testExtensionServiceIterator.hasNext()) {
            result.addAll(testExtensionServiceIterator.next().getExtensions());
        }
        return result;
    }

    public WeldSetupClass buildWeldSetup() {
        WeldSetupClass weldSetup = new WeldSetupClass();
        weldSetup.setBeanClasses(toBeStarted());
        weldSetup.setAlternativeClasses(getEnabledAlternatives());
        weldSetup.setEnabledAlternativeStereotypes(getEnabledAlternativeStereotypes());
        weldSetup.setExtensions(getExtensions());
        weldSetup.setEnabledDecorators(getDecorators());
        weldSetup.setEnabledInterceptors(getInterceptors());
        for (Extension e : getExtensionÓbjects())
            weldSetup.addExtensionObject(e);
        for (Extension e : findExtensions()) {
            weldSetup.addExtensionObject(e);
        }
        return weldSetup;
    }


    private Set<Class<?>> toBeStarted() {
        return this.builder.beansToBeStarted;
    }


    private List<Class<?>> getDecorators() {
        return builder.decorators;
    }

    private List<Class<?>> getInterceptors() {
        return builder.interceptors;
    }

}
