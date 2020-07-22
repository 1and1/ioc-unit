package com.oneandone.iocunit.analyzer;

import static java.lang.Integer.min;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.inject.Specializes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class Phase3Fixer extends PhasesBase {
    static Logger logger = LoggerFactory.getLogger(Phase3Fixer.class);

    public Phase3Fixer(Configuration configuration) {
        super(configuration);
    }

    public void work() {
        configuration.setPhase(Configuration.Phase.FIXING);
        logger.trace("Phase3Fixer starting");
        HashSet<Class<?>> newCandidates = new HashSet<>();
        HashMultiMap<QualifiedType, Class<?>> ambiguus = new HashMultiMap<>();
        HashMap<QualifiedType, QualifiedType> injectsDone = new HashMap<>();
        for (QualifiedType inject : configuration.getInjects()) {
            Set<QualifiedType> matching = configuration.getAvailableProducerMap().findMatchingProducersRegardingAlternatives(inject);
            if (matching.size() == 0)
                continue;
            matching = matching
                    .stream()
                    .filter(q -> !configuration.isExcluded(q.getDeclaringClass()))
                    .collect(Collectors.toSet());
            if (matching.size() == 1) {
                logger.trace("Found Fix for Inject {}",inject);
                QualifiedType producer = matching.iterator().next();
                Class declaringClass = producer.getDeclaringClass();
                if (configuration.isToBeStarted(declaringClass)) {
                    logger.error("Declaring Class {} already to be started", declaringClass.getName());
                } else if (newCandidates.contains(declaringClass)) {
                        logger.trace("Declaring Class {} already in new candidates", declaringClass.getName());
                } else {
                    logger.trace("Adding declaring Class {} to new candidates", declaringClass.getName());
                    configuration.candidate(declaringClass);
                    newCandidates.add(declaringClass);
                }
                if (configuration.didGuess && !injectsDone.containsKey(inject)) {
                    logger.trace("After guessing from Class: {}, producer: {} matches Inject {}.", declaringClass.getSimpleName(),
                            producer, inject);
                    logger.warn("To avoid guessing: add {} as SutClass or TestClass.", declaringClass.getName());
                }
                injectsDone.put(inject, producer);
            } else {
                if (!inject.isInstance()) {  // Search for specializing and make them higher prior
                    List<Class<?>> specializingCandidates  = new ArrayList<>();
                    QualifiedType firstQ = null;
                    for (QualifiedType q: matching) {
                        if (q.getDeclaringClass().getAnnotation(Specializes.class) != null) {
                            specializingCandidates.add(q.getDeclaringClass());
                            firstQ = q;
                        }
                    }
                    if (specializingCandidates.size() == 1) {
                        addToCandidates(newCandidates, specializingCandidates.get(0));
                        injectsDone.put(inject, firstQ);
                    } else if (specializingCandidates.size() > 1) {
                        matching = matching
                                .stream()
                                .filter(q -> q.getDeclaringClass().getAnnotation(Specializes.class) != null)
                                .collect(Collectors.toSet());
                    }

                }
            }
        }

        injectsDone.entrySet().forEach(entry -> configuration.injectHandled(entry.getKey(), entry.getValue()));

        for (QualifiedType inject : configuration.getInjects()) {
            if (inject.isInstance())
                continue;
            Set<QualifiedType> matching = configuration.getAvailableProducerMap().findMatchingProducersRegardingAlternatives(inject);
            if (matching.size() == 0)
                continue;
            matching = matching
                    .stream()
                    .filter(q -> !configuration.isExcluded(q.getDeclaringClass()))
                    .collect(Collectors.toSet());
            Map<Boolean, List<QualifiedType>> testClassBacked = matching
                    .stream()
                    .collect(Collectors.groupingBy(
                            match -> configuration.isTestClass(match.getDeclaringClass())));
            final List<QualifiedType> testClassBackedProducers = testClassBacked.get(true);
            if(testClassBackedProducers != null && testClassBackedProducers.size() > 0) {
                if(testClassBackedProducers.size() > 1) {
                    logger.error("More than one available Testclass available to produce: {}", inject);
                }
                final Class declaringClass = testClassBackedProducers.iterator().next().getDeclaringClass();
                logger.trace("Selected test class: {}", declaringClass.getName());
                configuration.candidate(declaringClass);
            }
            else {
                final List<QualifiedType> sutClassBackedProducers = testClassBacked.get(false);
                if(sutClassBackedProducers != null) {
                    if (sutClassBackedProducers.size() > 1) {
                        logger.warn("More than one available Sutclass available to produce: {}", inject);
                        if (ConfigStatics.isParameterizedType(inject.getDeclaringClass())) {
                            for (QualifiedType q : sutClassBackedProducers) {
                                logger.warn("-- : {}", q);
                                addToCandidates(newCandidates, q.getDeclaringClass());
                            }
                            logger.warn("Added all of them to candidates since declaring class is generic.");
                        } else {
                            Optional<QualifiedType> oneAlreadyThere = sutClassBackedProducers
                                    .stream()
                                    .filter(q -> configuration.isToBeStarted(q.getDeclaringClass()) ||
                                                 configuration.isCandidate(q.getDeclaringClass()))
                                    .findAny();
                            if(oneAlreadyThere.isPresent()) {
                                logger.warn("Chose one because of backing class {} already there", oneAlreadyThere.get());
                            }
                            else {
                                sutClassBackedProducers.forEach(q -> {
                                    ambiguus.put(inject, q.getDeclaringClass());
                                });
                            }
                        }
                    } else {
                        final Class declaringClass = sutClassBackedProducers.iterator().next().getDeclaringClass();
                        logger.trace("Selected sut class: {}", declaringClass.getName());
                        addToCandidates(newCandidates, declaringClass);
                    }
                }
            }
        }
        if (ambiguus.size() > 0) {
            /**
             * If two different classes are started which back producers to the same inject it comes to ambiguity problems
             * So search for that case.
             * Search for the best solution: the backing class which is usable for the most injects.
             */
            List<List<Class<?>>> classesArrayList = new ArrayList();

            for (QualifiedType inject : ambiguus.keySet()) {
                ArrayList<Class<?>> al = new ArrayList<>();
                al.addAll(ambiguus.get(inject));
                classesArrayList.add(al);
            }

            Set<Class<?>> solution = optimizeUsage(classesArrayList);
            if (solution.isEmpty())
                logger.error("Did not find solution");
            else {
                if (logger.isTraceEnabled()) {
                    for (Class<?> c: solution) {
                        logger.trace("Chose: {}", c.getName());
                    }
                }
            }
            for (Class<?> c: solution) {
                logger.trace("Optimize usage selected test? {} class: {}", configuration.isTestClass(c), c);
                configuration.candidate(c);
            }

        }
        logger.trace("Phase3Fixer ready");

    }

    private void addToCandidates(final HashSet<Class<?>> newCandidates, final Class declaringClass) {
        if (newCandidates.contains(declaringClass)) {
            logger.trace("Declaring Class {} already in new candidates", declaringClass.getName());
        } else {
            newCandidates.add(declaringClass);
            configuration.candidate(declaringClass);
        }
    }

    /**
     * for each entry of input choose one element from the candidates
     * @param input testerExtensionsConfigsFinder list of non empty sets of unique candidates. These sets might share elements.
     * @param <T> the type of the elements
     * @return the combination of single elements of the sets so that: if two sets share  elements, the solution
     * for these two sets must contain exactly one of these shared elements.
     */
    public <T> Set<T> optimizeUsage(List<List<T>> input) {
        int entrynum = input.size();
        int pos[] = new int[entrynum];
        ArrayList<T> solution = new ArrayList();

        ArrayList<ArrayList<T>> classesArrayList = new ArrayList();
        ArrayList<ArrayList<T>> orgClassesArrayList = new ArrayList();
        int index = 0;
        for (List<T> t : input) {
            ArrayList<T> al = new ArrayList<>();
            al.addAll(t);
            classesArrayList.add(al);
            orgClassesArrayList.add((ArrayList<T>) al.clone());
            pos[index] = 0;
            solution.add(null);
        }
        boolean doBackTrack = false;
        int currentIndex = min(0,classesArrayList.size() - 1);

        while (currentIndex >= 0 && currentIndex < entrynum) {
            doBackTrack = false;
            ArrayList<T> classes = (ArrayList<T>) classesArrayList.get(currentIndex).clone();
            T chosen = classes.get(pos[currentIndex]);
            solution.set(currentIndex, chosen);
            for (int j = currentIndex+1; j < entrynum; j++) {
                List<T> entry = classesArrayList.get(j);
                if (entry.contains(chosen)) {
                    classes.addAll(entry);
                    entry.clear();
                    entry.add(chosen);
                }
            }
            for (int j = currentIndex+1; j < entrynum; j++) {
                List<T> entry = classesArrayList.get(j);
                if (!entry.contains(chosen)) {
                    for (T c : classes) {
                        entry.remove(c);
                    }
                    if (entry.isEmpty()) {
                        doBackTrack = true;
                    }
                }
            }


            if (doBackTrack) {
                for (int j = currentIndex+1; j < entrynum; j++) {
                    classesArrayList.set(j, (ArrayList<T>) (orgClassesArrayList.get(j).clone()));
                }
                if (pos[currentIndex] >= classesArrayList.get(currentIndex).size()-1) {
                    pos[currentIndex] = 0;  // reset
                    classesArrayList.set(currentIndex, (ArrayList<T>) (orgClassesArrayList.get(currentIndex).clone()));
                    solution.set(currentIndex, null); // fix solution
                    currentIndex --;
                    if (currentIndex < 0)
                        return Collections.EMPTY_SET;
                } else {
                    pos[currentIndex]++;
                }
            } else {
                currentIndex ++;
            }
        }
        return solution.stream().collect(Collectors.toSet());
    }
}
