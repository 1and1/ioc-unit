package com.oneandone.iocunit.analyzer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The map contained in this class should allow it to find "Injection-Candidates".
 * The map is filled by QualifiedType-Objects which might work as producers during
 * CDI-Resolution. The Basetype and all its superclasses are used as keys. To actally verify,
 * that testerExtensionsConfigsFinder result of testerExtensionsConfigsFinder get to ProducerMap can be used, further checks of ParameterTypes, Qualifiers and Alternative
 * Annotations are necessary.
 *
 * @author aschoerk
 */
public class ProducerMap {
    private String mapName = "Unknown";
    private Logger log = null;
    private Map<String, Set<QualifiedType>> map = new HashMap<>();
    private Configuration configuration;

    public ProducerMap(final Configuration configuration, String mapName) {
        this.configuration = configuration;
        this.mapName = mapName;
        this.log = LoggerFactory.getLogger(ProducerMap.class.getName() + "_" + mapName);
    }

    public Set<QualifiedType> findMatchingProducersRegardingAlternatives(final QualifiedType inject) {
        Set<QualifiedType> matching = new HashSet<>();

        Set<QualifiedType> producers = get(inject.getRawtype().getCanonicalName());
        // check types and qualifiers of results
        for (QualifiedType qp : producers) {
            if(!configuration.getExcludedClasses().contains(qp.getDeclaringClass())) {
                if(qp.isAssignableTo(inject)) {
                    log.trace("Qualified Match {} ", qp);
                    matching.add(qp);
                }
            }
            else {
                log.info("Ignored producer because of excluded declaring class: {}", qp);
            }
        }
        leaveOnlyEnabledAlternativesIfThereAre(matching);
        return matching;
    }

    /**
     * The Matching Producers might be Alternatives. If so, then check if any of them are enabled. If so then remove the non-alternatives, and the not
     * enabled ones. If no enabled alternative is there, then remove all alternatives.
     *
     * @param matchingProducers
     */
    public void leaveOnlyEnabledAlternativesIfThereAre(Set<QualifiedType> matchingProducers) {
        Set<QualifiedType> alternatives = matchingProducers
                .stream()
                .filter(q -> q.isAlternative())
                .collect(Collectors.toSet());
        if(!alternatives.isEmpty()) {
            Set<QualifiedType> activeAlternatives = new HashSet<>();

            for (QualifiedType a : alternatives) {
                ConfigStatics.logger.trace("Matching alternative: {}", a);
                Class declaringClass = a.getDeclaringClass();
                if(a.getAlternativeStereotype() != null) {
                    if(configuration.isActiveAlternativeStereoType(a.getAlternativeStereotype())) {
                        ConfigStatics.logger.trace("Found StereotypeAlternative in Class {}: {} ", declaringClass.getSimpleName(), a);
                        activeAlternatives.add(a);
                    }
                    else {
                        continue;
                    }
                }
                else if(configuration.isEnabledAlternative(declaringClass)) {
                    ConfigStatics.logger.trace("Found Alternative in Class {}: {} ", declaringClass.getSimpleName(), a);
                    activeAlternatives.add(a);
                }
                else {
                    ConfigStatics.logger.warn("Not used Alternative Candidate in Class {}: {} ", declaringClass.getSimpleName(), a);
                    continue;
                }
            }
            if(activeAlternatives.size() > 0) {
                matchingProducers.clear();
                matchingProducers.addAll(activeAlternatives);
            }
            else {
                matchingProducers.removeAll(alternatives);
            }
        }
    }

    Set<QualifiedType> get(String c) {
        Set<QualifiedType> tmp = map.get(c);
        if (tmp == null) {
            return Collections.EMPTY_SET;
        } else {
            return tmp;
        }
    }

    void addToProducerMap(Class c, QualifiedType q) {
        log.trace("adding: {}/test?{}/{} ", c.getCanonicalName(), configuration.isTestClass(q.getDeclaringClass()), q);
        Set<QualifiedType> existing = map.get(c.getCanonicalName());
        if (existing == null) {
            existing = new HashSet<>();
            map.put(c.getCanonicalName(), existing);
        }
        existing.add(q);
    }

    void addInterfaceToProducerMap(Class iface, QualifiedType q) {
        addToProducerMap(iface, q);
        Class[] interfaces = iface.getInterfaces();
        for (Class subiface : interfaces) {
            addInterfaceToProducerMap(subiface, q);
        }
    }

    void addToProducerMap(QualifiedType q) {
        Class c = q.getRawtype();
        if (c.isInterface()) {
            addInterfaceToProducerMap(c, q);
        } else {
            while (c != null && !c.equals(Object.class)) {
                addToProducerMap(c, q);
                Class[] interfaces = c.getInterfaces();
                for (Class iface : interfaces) {
                    addInterfaceToProducerMap(iface, q);
                }
                c = c.getSuperclass();
            }

        }
    }

}
