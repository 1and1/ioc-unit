package com.oneandone.cdi.testanalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class InjectsMatcher {
    static AtomicInteger instance = new AtomicInteger(0);
    Logger log = LoggerFactory.getLogger("InjectMatching" + instance.addAndGet(1));
    HashMultiMap<QualifiedType, QualifiedType> matching = new HashMultiMap<>();
    HashMultiMap<QualifiedType, QualifiedType> ambiguus = new HashMultiMap<>();
    Set<QualifiedType> empty = new HashSet<>();
    LeveledBuilder builder;

    public InjectsMatcher(final LeveledBuilder builder) {
        this.builder = builder;
    }

    public void match() {
        log.debug("Starting matching");
        for (QualifiedType inject : builder.injections) {
            matchInject(inject);
        }
        log.debug("Ready    matching");
    }

    public void matchInject(QualifiedType inject) {
        log.trace("matchingInject: {}", inject);
        Set<QualifiedType> producers = builder.producerMap.get(inject.getRawtype());
        if (producers == null)
            return;
        // check types and qualifiers of results
        for (QualifiedType qp : producers) {
            if (!builder.excludedClasses.contains(qp.getDeclaringClass())) {
                if (qp.isAssignableTo(inject)) {
                    log.trace("Qualified Match {} ", qp);
                    matching.put(inject, qp);
                }
            } else {
                log.info("Ignored producer because of excluded declaring class: {}", qp);
            }
        }
        leaveOnlyEnabledAlternativesIfThereAre(matching.getValues(inject));
        if (matching.getValues(inject).size() == 0) {
            log.info("No match found for inject {}", inject);
            empty.add(inject);
            matching.remove(inject);
        } else if (matching.getValues(inject).size() > 1) {
            for (QualifiedType x : matching.get(inject)) {
                log.info("Ambiguus match: {} for inject", x, inject);
            }
            ambiguus.put(inject, matching.get(inject));
            matching.remove(inject);
        } else {
            log.trace("Unambiguus match: {}", matching.get(inject).iterator().next());
        }
    }


    /**
     * Class to be started can be removed, if it is optional and everything that it produces is also produced by other classes TODO: handle
     * newAlternatives
     * 
     * @param classesToStart
     */
    public void matchHandledInject(Set<?> classesToStart) {
        HashMultiMap<Class<?>, QualifiedType> declaringClass2Injects = new HashMultiMap<>();
        HashMultiMap<Class<?>, QualifiedType> declaringClass2Producers = new HashMultiMap<>();

        final HashMultiMap<QualifiedType, QualifiedType> minimizeMap = InjectsMinimizer.minimize(builder.handledInjections, builder);
        for (QualifiedType inject : minimizeMap.keySet()) {
            Set<QualifiedType> producers = builder.producerMap.get(inject.getRawtype());
            for (QualifiedType producer : producers) {
                Class declaringClass = producer.getDeclaringClass();
                if (builder.beansToBeStarted.contains(declaringClass)) {
                    if (producer.isAssignableTo(inject)) {
                        declaringClass2Injects.put(declaringClass, inject);
                        declaringClass2Producers.put(declaringClass, producer);
                    }
                }
            }
        }
        List<Class<?>> optionalClasses = declaringClass2Injects.keySet()
                .stream()
                .filter(c -> !builder.isObligatoryClass(c))
                .collect(Collectors.toList());

        Set<Class> classesToBeRemoved = new HashSet<>();

        for (Class c : optionalClasses) {
            for (Class d : declaringClass2Injects.keySet()) {
                if (!(c.equals(d) || classesToBeRemoved.contains(d))) {
                    Set<QualifiedType> cValues = declaringClass2Injects.getValues(c);
                    Set<QualifiedType> dValues = declaringClass2Injects.getValues(d);
                    if (dValues.containsAll(cValues)) {
                        if (!cValues.containsAll(dValues)) {
                            classesToBeRemoved.add(c);
                            log.info("Class {} to be removed from classes to be started.", c);
                        } else {
                            for (QualifiedType t : cValues) {
                                Set<QualifiedType> injects = minimizeMap.get(t);
                                int cProduces = 0;
                                int dProduces = 0;
                                for (QualifiedType inject : injects) {
                                    for (QualifiedType producerC : declaringClass2Producers.get(c)) {
                                        if (producerC.isAssignableTo(inject))
                                            cProduces++;
                                    }
                                    for (QualifiedType producerD : declaringClass2Producers.get(d)) {
                                        if (producerD.isAssignableTo(inject))
                                            dProduces++;
                                    }
                                }
                                if (dProduces > cProduces) {
                                    classesToBeRemoved.add(c);
                                    log.info("Class {} to be removed from classes to be started.", c);
                                } else if (dProduces < cProduces && !builder.isObligatoryClass(d)) {
                                    classesToBeRemoved.add(d);
                                    log.info("Class {} to be removed from classes to be started.", d);
                                } else {
                                    log.error("Could not decide which class to remove: {},  {}", c, d);
                                }
                            }
                        }
                    }
                }
            }
        }
        // at the moment only classes without producers used for injects or classes with single producers used for injects
        // classes only added in Availability not obligatory.
        classesToStart.removeAll(classesToBeRemoved);
    }

    /**
     * The Matching Producers might be Alternatives. If so, then check if any of them are enabled. If so then remove the non-alternatives, and the not
     * enabled ones. If no enabled alternative is there, then remove all alternatives.
     *
     * @param matchingProducers
     */
    private void leaveOnlyEnabledAlternativesIfThereAre(Set<QualifiedType> matchingProducers) {
        Set<QualifiedType> alternatives = matchingProducers
                .stream()
                .filter(q -> q.isAlternative())
                .collect(Collectors.toSet());
        if (!alternatives.isEmpty()) {
            Set<QualifiedType> activeAlternatives = new HashSet<>();

            for (QualifiedType a : alternatives) {
                log.trace("Matching alternative: {}", a);
                Class declaringClass = a.getDeclaringClass();
                if (a.getAlternativeStereotype() != null) {
                    if (builder.isActiveAlternativeStereoType(a.getAlternativeStereotype())) {
                        log.trace("Found StereotypeAlternative in Class {}: {} ", declaringClass.getSimpleName(), a);
                        activeAlternatives.add(a);
                    } else
                        continue;
                } else if (builder.isAlternative(declaringClass)) {
                    log.trace("Found Alternative in Class {}: {} ", declaringClass.getSimpleName(), a);
                    activeAlternatives.add(a);
                } else {
                    log.info("Not used Alternative Candidate in Class {}: {} ", declaringClass.getSimpleName(), a);
                    continue;
                }
            }
            if (activeAlternatives.size() > 0) {
                matchingProducers.clear();
                matchingProducers.addAll(activeAlternatives);
            } else {
                matchingProducers.removeAll(alternatives);
            }
        }
    }



    public List<Class<?>> evaluateMatches(final List<CdiConfigCreator.ProblemRecord> problems) {
        Set<Class<?>> newToBeStarted = new HashSet();
        for (QualifiedType inject : empty) {
            // search for producers and inner classes
        }

        Set<QualifiedType> chosenTypes = new HashSet<>();

        for (QualifiedType inject : matching.keySet()) {
            final QualifiedType producingType = matching.getValues(inject).iterator().next();
            if (!builder.beansToBeStarted.contains(producingType.getDeclaringClass())) {
                log.trace("Unambiguus Producer for Inject {}", inject);
                log.trace("--- {}", producingType);
                newToBeStarted.add(producingType.getDeclaringClass());
                chosenTypes.add(producingType);
            }
            builder.injectHandled(inject);
        }

        for (QualifiedType inject : ambiguus.keySet()) {
            Set<Class<?>> testClasses = new HashSet<>();
            Set<Class<?>> sutClasses = new HashSet<>();
            Set<Class<?>> availableTestClasses = new HashSet<>();
            Set<Class<?>> availableClasses = new HashSet<>();
            Set<QualifiedType> producingTypes = ambiguus.get(inject);
            log.info("Ambiguus resolved inject: {}", inject);
            for (QualifiedType producing : producingTypes) {
                log.info("--- Producing: {}", producing);
            }
            Set<QualifiedType> alreadyChosen = producingTypes.stream()
                    .filter(p -> chosenTypes.contains(p))
                    .collect(Collectors.toSet());
            if (alreadyChosen.size() > 0) {
                if (alreadyChosen.size() > 1) {
                    log.error("Two producing types should only resolve to one chosen for inject {}", inject);
                }
                for (QualifiedType q : alreadyChosen) {
                    log.info("Already chosen: {}", q);
                }
                continue;
            }
            boolean alreadyProduced = false;
            for (QualifiedType q : producingTypes) {
                Class declaringClass = q.getDeclaringClass();
                assert declaringClass != null;
                assert !builder.excludedClasses.contains(declaringClass);
                if (builder.beansToBeStarted.contains(declaringClass) || newToBeStarted.contains(declaringClass)) {
                    alreadyProduced = true;
                } else if (builder.isTestClass(declaringClass)) {
                    testClasses.add(declaringClass);
                } else if (builder.isSuTClass(declaringClass)) {
                    sutClasses.add(declaringClass);
                } else if (builder.isTestClassAvailable(declaringClass)) {
                    availableTestClasses.add(declaringClass);
                } else {
                    availableClasses.add(declaringClass);
                }
            }
            if (alreadyProduced) {
                ; // inject handled by producer in already used class.
                builder.injectHandled(inject);
            } else if (testClasses.size() != 0) {
                if (testClasses.size() > 1 || sutClasses.size() != 0) {
                    problems.add(new CdiConfigCreator.ProblemRecord("Handling Inject: {} Testclass(es) {} clashing with SutClass(es) {}",
                            inject, testClasses, sutClasses));
                } else {
                    final Class<?> testClass = testClasses.iterator().next();
                    if (!builder.beansToBeStarted.contains(testClass)) {
                        newToBeStarted.add(testClass);
                    }
                }
                builder.injectHandled(inject);
            } else if (sutClasses.size() > 0) {
                if (sutClasses.size() > 1)
                    problems.add(new CdiConfigCreator.ProblemRecord("Handling Inject: {} too many SutClass(es) {}",
                            inject, sutClasses));
                final Class<?> sutClass = sutClasses.iterator().next();
                if (!builder.beansToBeStarted.contains(sutClass)) {
                    newToBeStarted.add(sutClass);
                }
                builder.injectHandled(inject);
            } else if (availableTestClasses.size() > 0) {
                if (availableTestClasses.size() > 1)
                    problems.add(new CdiConfigCreator.ProblemRecord("Handling Inject: {} more than one available TestClass(es) {}",
                            inject, availableTestClasses));
                final Class<?> testClass = availableTestClasses.iterator().next();
                if (!builder.beansToBeStarted.contains(testClass)) {
                    builder.testClass(testClass);
                    newToBeStarted.add(testClass);
                }
            } else {
                assert (availableClasses.size() != 0);
                if (availableClasses.size() > 1) {
                    problems.add(new CdiConfigCreator.ProblemRecord("Handling Inject: {} more than one AvailableClass(es) {}",
                            inject, availableClasses));
                }
                final Class<?> toBeStarted = availableClasses.iterator().next();
                builder.sutClass(toBeStarted);
                newToBeStarted.add(toBeStarted);
            }
        }
        List<Class<?>> result = new ArrayList<>(newToBeStarted);
        return result;
    }
}
