package com.oneandone.iocunit.analyzer;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.iocunit.analyzer.extensions.TestScopeExtension;

/**
 * @author aschoerk
 */
public class ConfigCreator extends ConfigCreatorBase {

    static private Logger logger = LoggerFactory.getLogger(ConfigCreator.class);

    Configuration configuration = new Configuration();

    private void init(InitialConfiguration initial, TesterExtensionsConfigsFinder testerExtensionsConfigsFinder) {
        if(initial.testClass != null) {
            if(initial.testClass.getAnnotation(ApplicationScoped.class) == null) {
                configuration.getElseClasses().extensionObjects.add(new TestScopeExtension(initial.testClass));
            }
            configuration.testClass(initial.testClass).candidate(initial.testClass);
        }
        configuration.setTesterExtensionsConfigsFinder(testerExtensionsConfigsFinder);

        configuration.testClassCandidates(testerExtensionsConfigsFinder.initialClasses)
                .testClassCandidates(initial.initialClasses)
                .testClassCandidates(initial.testClasses);
        for (Class<?> c : initial.sutClasses) {
            configuration
                    .candidate(c);
        }
        for (Class<?> c : initial.enabledAlternatives) {
            configuration.testClass(c)
                    .candidate(c)
                    .enabledAlternative(c);
        }
        if(initial.excludedClasses != null) {
            for (Class<?> c : initial.excludedClasses) {
                configuration.excluded(c);
            }
        }
        if(initial.sutClasspath != null && !initial.sutClasspath.isEmpty()) {
            throw new RuntimeException("not implemented yet");
            // addClasspaths(setToArray(initial.sutClasspath), true);
        }
        if(initial.testClasspath != null && !initial.testClasspath.isEmpty()) {
            throw new RuntimeException("not implemented yet");
            // addClasspaths(setToArray(initial.testClasspath), false);
        }
        if(initial.sutPackages != null && !initial.sutPackages.isEmpty()) {
            throw new RuntimeException("not implemented yet");
            // addPackages(setToArray(initial.sutPackages), true);
        }
        if(initial.testPackages != null && !initial.testPackages.isEmpty()) {
            throw new RuntimeException("not implemented yet");
            // addPackages(setToArray(initial.sutPackages), false);
        }

    }

    public void create(InitialConfiguration initial) {
        this.init(initial, new TesterExtensionsConfigsFinder());
        Phase1Analyzer phase1Analyzer = new Phase1Analyzer(configuration);
        Set<Class<?>> handledPhase4Classes = new HashSet<>();
        do {
            if(phase1Analyzer.work()) {
                new Phase2Matcher(configuration).work();
                new Phase3Fixer(configuration).work();
                if (configuration.emptyCandidates() && configuration.getInjects().size() > 0) {
                    Set<Class<?>> newClasses = new HashSet<>();
                    Set<Class<?>> newTestClasses = new HashSet<>();
                    for (QualifiedType q : configuration.getInjects()) {
                        final Class rawtype = q.getRawtype();
                        if(!(rawtype.isPrimitive()
                             || newClasses.contains(rawtype)
                             || rawtype.getName().startsWith("java.lang")
                             || handledPhase4Classes.contains(rawtype)
                        )) {
                            try {
                                URL rawtypePath = ClasspathHandler.getPath(rawtype);
                                if(ConfigStatics.mightBeBean(rawtype)) {
                                    configuration.candidate(rawtype);
                                }
                                else if(configuration.getTestClassPaths().contains(rawtypePath)) {
                                    ClasspathHandler.addClassPath(rawtype, newTestClasses);
                                }
                                else {
                                    ClasspathHandler.addClassPath(rawtype, newClasses);
                                }
                            } catch (MalformedURLException | NullPointerException e) {
                                ;
                            }
                        }
                    }
                    if (configuration.emptyCandidates()) {
                        addAvailableClasses(phase1Analyzer, newClasses, true);
                        addAvailableClasses(phase1Analyzer, newTestClasses, false);
                        new Phase3Fixer(configuration).work();
                        handledPhase4Classes.addAll(newClasses);
                        handledPhase4Classes.addAll(newTestClasses);
                    }
                }

                logger.trace("One Level done candidates size: {} injects.size: {}", !configuration.emptyCandidates(), configuration.getInjects().size());
            }
            else {
                break;
            }
        }
        while (!configuration.emptyCandidates());
        new

                Phase4Warner(initial, configuration).

                work();

    }

    private void addAvailableClasses(final Phase1Analyzer phase1Analyzer, final Set<Class<?>> newClasses, final boolean isSut) {
        newClasses.removeAll(configuration.getObligatory());
        newClasses.removeAll(configuration.getExcludedClasses());
        if(newClasses.size() > 0) {
            phase1Analyzer.extend(newClasses, isSut);
        }
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public WeldSetupClass buildWeldSetup(Method method) {

        return new SetupCreator(configuration).buildWeldSetup(method);
    }
}
