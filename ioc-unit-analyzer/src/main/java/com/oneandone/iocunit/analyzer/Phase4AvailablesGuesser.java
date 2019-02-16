package com.oneandone.iocunit.analyzer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class Phase4AvailablesGuesser extends PhasesBase {
    private final Phase1Analyzer phase1Analyzer;
    Logger logger = LoggerFactory.getLogger(Phase4AvailablesGuesser.class);
    Set<Class<?>> alreadyLogged = new HashSet<>();

    public Phase4AvailablesGuesser(final Configuration configuration, Phase1Analyzer phase1Analyzer) {
        super(configuration);
        this.phase1Analyzer = phase1Analyzer;
    }

    Set<Class<?>> handledPhase4Classes = new HashSet<>();

    public void work() {
        if (configuration.emptyCandidates() && configuration.getInjects().size() > 0) {

            Set<Class<?>> newClasses = new HashSet<>();
            Set<Class<?>> newTestClasses = new HashSet<>();

            for (QualifiedType q : configuration.getInjects()) {
                // if (q.isInstance())
                //   continue;
                final Class rawtype = q.getRawtype();
                final String rawtypeName = rawtype.getName();
                if(!(rawtype.isPrimitive()
                     || newClasses.contains(rawtype)
                     || rawtypeName.startsWith("java.lang.")
                     || rawtypeName.startsWith("javax.")
                     || rawtypeName.startsWith("org.jboss.")
                     || handledPhase4Classes.contains(rawtype)
                )) {
                    try {
                        URL rawtypePath = ClasspathHandler.getPath(rawtype);
                        if(ConfigStatics.mightBeBean(rawtype)) {
                            if (!alreadyLogged.contains(rawtype))
                                logger.warn("Added candidate {} in cdi-unit manner, even if not found as available", rawtype);
                            configuration.candidate(rawtype);
                        }
                        else if(configuration.getTestClassPaths().contains(rawtypePath)) {
                            if (!alreadyLogged.contains(rawtype))
                                logger.warn("Added classpath of testclass: {} even if not found as available for inject: {}", rawtype,q);
                            ClasspathHandler.addClassPath(rawtype, newTestClasses);
                        }
                        else {
                            if (!alreadyLogged.contains(rawtype))
                                logger.warn("Added classpath of sutclass: {} even if not found as available for inject: {}", rawtype,q);
                            ClasspathHandler.addClassPath(rawtype, newClasses);
                        }
                        alreadyLogged.add(rawtype);
                    } catch (MalformedURLException | NullPointerException e) {
                        ;
                    }
                }
            }
            if(configuration.emptyCandidates()) {
                addAvailableClasses(newClasses, true);
                addAvailableClasses(newTestClasses, false);
                new Phase3Fixer(configuration).work();
                handledPhase4Classes.addAll(newClasses);
                handledPhase4Classes.addAll(newTestClasses);
            }
        }
    }

    private void addAvailableClasses(final Set<Class<?>> newClasses, final boolean isSut) {
        newClasses.removeAll(configuration.getObligatory());
        newClasses.removeAll(configuration.getExcludedClasses());
        if(newClasses.size() > 0) {
            phase1Analyzer.extend(newClasses, isSut);
        }
    }

}
