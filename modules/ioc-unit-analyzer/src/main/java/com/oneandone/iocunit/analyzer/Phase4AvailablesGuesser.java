package com.oneandone.iocunit.analyzer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class Phase4AvailablesGuesser extends PhasesBase {
    private final Phase1Analyzer phase1Analyzer;
    Logger logger = LoggerFactory.getLogger(Phase4AvailablesGuesser.class);
    Map<Class<?>, Pair<Set<Class<?>>, Set<Class<?>>>> alreadyLogged = new HashMap<>();

    public Phase4AvailablesGuesser(final Configuration configuration, Phase1Analyzer phase1Analyzer) {
        super(configuration);
        this.phase1Analyzer = phase1Analyzer;
    }

    Set<Class<?>> handledPhase4Classes = new HashSet<>();

    public boolean rawTypeIgnore(String rawTypePath) {
        return rawTypePath.contains("/net/oneandone/ioc-unit/");
    }

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
                     || q.isInstance()
                )) {
                    try {
                        URL rawtypePath = ClasspathHandler.getPath(rawtype);
                        if (!rawTypeIgnore(rawtypePath.getPath())) {
                            Pair<Set<Class<?>>, Set<Class<?>>> history = alreadyLogged.get(rawtype);
                            if(ConfigStatics.mightBeBean(rawtype)) {
                                if(history == null) {
                                    logger.warn("Added candidate {} in cdi-unit manner, even if not found as available", rawtype);
                                    alreadyLogged.put(rawtype, Pair.of(null, null));
                                }
                                configuration.candidate(rawtype);
                            }
                            else if(configuration.getTestClassPaths().contains(rawtypePath)) {
                                Set<Class<?>> tmpTestClasses = new HashSet<>();
                                if(history == null) {
                                    logger.warn("Added classpath of testclass: {} even if not found as available for inject: {}", rawtype, q);
                                    ClasspathHandler.addClassPath(rawtype, tmpTestClasses, "");
                                    newTestClasses.addAll(tmpTestClasses);
                                    alreadyLogged.put(rawtype, Pair.of(tmpTestClasses, null));
                                } else {
                                    if (history.getLeft() == null) {
                                        ClasspathHandler.addClassPath(rawtype, tmpTestClasses, "");
                                        newTestClasses.addAll(tmpTestClasses);
                                        alreadyLogged.put(rawtype, Pair.of(tmpTestClasses,history.getRight()));
                                    } else {
                                        newTestClasses.addAll(history.getLeft());
                                    }
                                }
                            }
                            else {
                                Set<Class<?>> tmpClasses = new HashSet<>();
                                if(history == null) {
                                    logger.warn("Added classpath of sutclass: {} even if not found as available for inject: {}", rawtype, q);
                                    ClasspathHandler.addClassPath(rawtype, tmpClasses, "");
                                    newClasses.addAll(tmpClasses);
                                    alreadyLogged.put(rawtype, Pair.of(null, tmpClasses));
                                } else {
                                    if (history.getRight() == null) {
                                        ClasspathHandler.addClassPath(rawtype, tmpClasses, "");
                                        newTestClasses.addAll(tmpClasses);
                                        alreadyLogged.put(rawtype, Pair.of(history.getLeft(), tmpClasses));
                                    } else {
                                        newTestClasses.addAll(history.getRight());
                                    }
                                }
                            }
                        }
                    } catch (MalformedURLException | NullPointerException e) {
                        ;
                    }
                }
            }
            if(configuration.emptyCandidates()) {
                configuration.didGuess = true;
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
