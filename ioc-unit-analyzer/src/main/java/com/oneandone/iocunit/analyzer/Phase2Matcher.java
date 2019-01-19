package com.oneandone.iocunit.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
class Phase2Matcher extends PhasesBase {
    static AtomicInteger instance = new AtomicInteger(0);
    static Logger logger = LoggerFactory.getLogger(Phase2Matcher.class);
    HashMap<QualifiedType, QualifiedType> matching = new HashMap<>();
    HashMultiMap<QualifiedType, QualifiedType> ambiguus = new HashMultiMap<>();
    Set<QualifiedType> empty = new HashSet<>();

    public Phase2Matcher(Configuration configuration) {
        super(configuration);
    }

    public void matchInject(QualifiedType inject) {
        logger.trace("matchingInject: {}", inject);
        Set<QualifiedType> matchingProducers = configuration.getProducerMap().findMatchingProducersRegardingAlternatives(inject);
        if(matchingProducers.size() == 0) {
            logger.trace("No match found for inject {}", inject);
            empty.add(inject);
        }
        else if(matchingProducers.size() > 1) {
            for (QualifiedType x : matchingProducers) {
                logger.trace("Ambiguus match: {} for inject", x, inject);
            }
            ambiguus.put(inject, matchingProducers);
        }
        else {
            final QualifiedType theMatch = matchingProducers.iterator().next();
            matching.put(inject, theMatch);
            logger.trace("Unambiguus match: {}", theMatch);
        }
    }

    public List<Class<?>> evaluateMatches() {
        Set<Class<?>> newToBeStarted = new HashSet();
        for (QualifiedType inject : empty) {
            // search for producers and inner classes
        }

        Set<QualifiedType> chosenTypes = new HashSet<>();

        for (QualifiedType inject : matching.keySet()) {
            final QualifiedType producingType = matching.get(inject);
            if(!configuration.isToBeStarted(producingType.getDeclaringClass())) {
                if(producingType.isFake()) {
                    logger.trace("Fake Unambiguus Producer for Inject {}", inject, producingType);
                }
                else {
                    logger.trace("Unambiguus Producer for Inject {}", inject);
                    logger.trace("--- {}", producingType);
                    newToBeStarted.add(producingType.getDeclaringClass());
                    chosenTypes.add(producingType);
                }
            }
            configuration.injectHandled(inject, producingType);
        }

        for (QualifiedType inject : ambiguus.keySet()) {

            Map<Class<?>, QualifiedType> testClasses = new HashMap<>();
            Map<Class<?>, QualifiedType> sutClasses = new HashMap<>();
            Set<QualifiedType> producingTypes = ambiguus.get(inject);
            if (!inject.isInstance()){
                logger.info("Ambiguus resolved inject: {}", inject);
                for (QualifiedType producing : producingTypes) {
                    logger.info("--- Producing: {}", producing);
                }
            }
            Set<QualifiedType> alreadyChosen = producingTypes.stream()
                    .filter(p -> chosenTypes.contains(p))
                    .collect(Collectors.toSet());
            if(alreadyChosen.size() > 0) {
                if(alreadyChosen.size() > 1) {
                    logger.error("Two producing types should only resolve to one chosen for inject {}", inject);
                }
                for (QualifiedType q : alreadyChosen) {
                    logger.info("Already chosen: {}", q);
                }
                continue;
            }
            boolean alreadyProduced = false;
            for (QualifiedType q : producingTypes) {
                Class declaringClass = q.getDeclaringClass();
                assert declaringClass != null;
                assert !configuration.getExcludedClasses().contains(declaringClass);
                if(configuration.isToBeStarted(declaringClass) || newToBeStarted.contains(declaringClass)) {
                    alreadyProduced = true;
                    configuration.injectHandled(inject, q);
                }
                else if(configuration.isTestClass(declaringClass)) {
                    testClasses.put(declaringClass, q);
                }
                else {
                    sutClasses.put(declaringClass, q);
                }
            }
            if(alreadyProduced) {
                ; // inject handled by producer in already used class.

            }
            else if(testClasses.size() != 0) {
                if(testClasses.size() > 1) {
                    logger.error("Handling Inject: {} Testclass(es) {} clashing",
                            inject, testClasses);
                }
                else {
                    if(sutClasses.size() > 0) {
                        logger.error("Handling Inject: {} Testclass {} clashing with sutClasses",
                                inject, testClasses, sutClasses);
                        for (Class<?> c : sutClasses.keySet()) {
                            Set<QualifiedType> injects = configuration.getInjectsForClass(c);
                            if(injects.size() == 0) {
                                logger.error("SutClass {} excluded because of clashing with Testclass {}"
                                        , c, testClasses.keySet().iterator().next());
                                configuration.excluded(c);
                                sutClasses.remove(c);
                            }
                            else {
                                logger.error("Tried to exclude SutClass {} excluded because of clashing "
                                                           + "with Testclass {} not possible because of other injects"
                                        , c, testClasses.keySet().iterator().next());
                            }
                        }
                    }
                    final Class<?> testClass = testClasses.keySet().iterator().next();
                    if(!configuration.isToBeStarted(testClass)) {
                        newToBeStarted.add(testClass);
                    }
                    configuration.injectHandled(inject, testClasses.values().iterator().next());
                }
            }
            else if(sutClasses.size() > 0) {
                if(sutClasses.size() > 1) {
                    logger.error("Handling Inject: {} too many SutClass(es) {}",
                            inject, sutClasses);
                }
                else {
                    final Class<?> sutClass = sutClasses.keySet().iterator().next();
                    if(!configuration.isToBeStarted(sutClass)) {
                        newToBeStarted.add(sutClass);
                    }
                    configuration.injectHandled(inject, sutClasses.values().iterator().next());
                }
            }
        }
        List<Class<?>> result = new ArrayList<>(newToBeStarted);
        return result;
    }

    public void work() {
        configuration.setPhase(Configuration.Phase.MATCHING);
        logger.trace("Phase2Matcher starting");
        for (QualifiedType i : configuration.getInjects()) {
            matchInject(i);

        }
        evaluateMatches();
        logger.trace("Phase2Matcher ready");
    }
}
