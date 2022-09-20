package com.oneandone.iocunit.analyzer;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.AddOpens;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.iocunit.analyzer.extensions.TestScopeExtension;

/**
 * @author aschoerk
 */
public class ConfigCreator extends ConfigCreatorBase {

    static private Logger logger = LoggerFactory.getLogger(ConfigCreator.class);

    Configuration configuration = null;

    private void init(InitialConfiguration initial, TesterExtensionsConfigsFinder testerExtensionsConfigsFinder) {
        AddOpens.open("java.base", "java.lang", "java.util");
        configuration = new Configuration(testerExtensionsConfigsFinder);
        configuration.setPhase(Configuration.Phase.INITIALIZING);
        if(initial.testClass != null) {
            if(initial.testClass.getAnnotation(ApplicationScoped.class) == null) {
                configuration.getElseClasses().extensionObjects.add(new TestScopeExtension(initial.testClass));
            }
            configuration.setTheTestClass(initial.testClass);
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
        Phase4AvailablesGuesser phase4AvailablesGuesser =
                configuration.allowGuessing? new Phase4AvailablesGuesser(configuration, phase1Analyzer) : null;
        do {
            configuration.setAvailablesChanged(false);
            if(phase1Analyzer.work()) {
                new Phase2Matcher(configuration).work();
                new Phase3Fixer(configuration).work();
                if(configuration.allowGuessing) {
                    phase4AvailablesGuesser.work();
                }
                logger.trace("One Level done candidates size: {} injects.size: {}", !configuration.emptyCandidates(), configuration.getInjects().size());
            }
            else {
                break;
            }
        }
        while (!configuration.emptyCandidates() || configuration.isAvailablesChanged());
        new Phase5Warner(initial, configuration).work();

    }



    public Configuration getConfiguration() {
        return configuration;
    }

    public WeldSetupClass buildWeldSetup(Method method) {

        return new SetupCreator(configuration).buildWeldSetup(method);
    }
}
