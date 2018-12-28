package com.oneandone.cdi.testanalyzer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class Phase3Fixer {
    private final Configuration configuration;
    static Logger logger = LoggerFactory.getLogger(Phase3Fixer.class);

    public Phase3Fixer(Configuration configuration) {
        this.configuration = configuration;
    }

    public void work() {
        for (QualifiedType inject : configuration.getInjects()) {
            Set<QualifiedType> matching = configuration.getAvailableProducerMap().findMatchingProducers(inject);
            if(matching.size() == 1) {
                QualifiedType producer = matching.iterator().next();
                if(configuration.getToBeStarted().contains(producer.getDeclaringClass())) {
                    logger.error("Phase3: Declaring Class already to be started {}", producer);
                }
                else {
                    configuration.candidate(producer.getDeclaringClass());
                }
            }
            else {
                Map<Boolean, List<QualifiedType>> testClassBacked = matching
                        .stream()
                        .collect(Collectors.groupingBy(
                                match -> configuration.isTestClass(match.getDeclaringClass())));
                final List<QualifiedType> testClassBackedProducers = testClassBacked.get(true);
                if(testClassBackedProducers.size() > 0) {
                    if(testClassBackedProducers.size() > 1) {
                        logger.error("More than one available Testclass available to produce: {}", inject);
                    }
                    configuration.candidate(testClassBackedProducers.iterator().next().getDeclaringClass());
                }
                else {
                    final List<QualifiedType> sutClassBackedProducers = testClassBacked.get(false);
                    if(sutClassBackedProducers.size() > 1) {
                        logger.error("More than one available Sutclass available to produce: {}", inject);
                    }
                    configuration.candidate(sutClassBackedProducers.iterator().next().getDeclaringClass());
                }
            }
        }

    }
}
