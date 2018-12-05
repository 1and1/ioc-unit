package com.oneandone.cdi.testanalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class InjectsMatcher {
    static Logger log = LoggerFactory.getLogger("InjectMatching");
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
        Set<QualifiedType> producers = builder.producerMap.get(inject.getRawtype());
        if (producers == null)
            return;
        // check types and qualifiers of results
        for (QualifiedType qp : producers) {
            if (qp.isAssignableTo(inject)) {
                log.debug("Qualified Match \n --- {} \n --- to inject: {}", qp, inject);
                matching.put(inject, qp);
            }
        }
        handleAlternatives(matching.getValues(inject));
        if (matching.getValues(inject).size() == 0) {
            log.info("No match found for {}", inject);
            empty.add(inject);
            matching.remove(inject);
        } else if (matching.getValues(inject).size() > 1) {
            log.info("Ambiguus matches found for \n --- inject: {}", inject);
            for (QualifiedType x : matching.get(inject)) {
                log.debug(" --- {}", x);
            }
            ambiguus.put(inject, matching.get(inject));
            matching.remove(inject);
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

    private void handleAlternatives(Set<QualifiedType> matching) {
        Optional<QualifiedType> optionalAlternative = matching.stream().filter(q -> q.isAlternative()).findAny();
        if (optionalAlternative.isPresent()) {
            QualifiedType alternative = optionalAlternative.get();
            if (alternative.getAlternativeStereotype() == null) {
                if (builder.enabledAlternatives.contains(alternative.getRawtype())) {
                    matching.clear();
                    matching.add(alternative);
                }
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
            Set<QualifiedType> alternatives = new HashSet<>();
            Set<QualifiedType> producingTypes = ambiguus.get(inject);
            Set<QualifiedType> alreadyChosen = producingTypes.stream()
                    .filter(p -> chosenTypes.contains(p))
                    .collect(Collectors.toSet());
            if (alreadyChosen.size() > 0) {
                continue;
            }
            boolean alreadyProduced = false;
            for (QualifiedType q : producingTypes) {
                Class declaringClass = q.getDeclaringClass();
                assert declaringClass != null;
                if (q.isAlternative()) {
                    if (q.getAlternativeStereotype() != null) {
                        if (builder.isActiveAlternativeStereoType(q.getAlternativeStereotype())) {
                            alternatives.add(q);
                        } else
                            continue;
                    } else if (builder.isAlternative(declaringClass)) {
                        alternatives.add(q);
                    } else {
                        continue;
                    }
                } else if (builder.beansToBeStarted.contains(declaringClass) || newToBeStarted.contains(declaringClass))
                    alreadyProduced = true;
                else if (builder.isTestClass(declaringClass)) {
                    testClasses.add(declaringClass);
                } else if (builder.isSuTClass(declaringClass)) {
                    sutClasses.add(declaringClass);
                } else if (builder.isTestClassAvailable(declaringClass)) {
                    availableTestClasses.add(declaringClass);
                } else {
                    availableClasses.add(declaringClass);
                }
            }
            if (alternatives.size() != 0) {
                if (alternatives.size() > 1) {
                    problems.add(new CdiConfigCreator.ProblemRecord("Handling Inject: {} more than one active Alternative {} ",
                            inject, alternatives));
                }
                builder.injectHandled(inject);
            } else if (alreadyProduced) {
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
