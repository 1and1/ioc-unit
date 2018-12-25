package com.oneandone.cdi.testanalyzer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The map contained in this class should allow it to find "Injection-Candidates".
 * The map is filled by QualifiedType-Objects which might work as producers during
 * CDI-Resolution. The Basetype and all its superclasses are used as keys. To actally verify,
 * that a result of a get to ProducerMap can be used, further checks of ParameterTypes, Qualifiers and Alternative
 * Annotations are necessary.
 *
 * @author aschoerk
 */
public class ProducerMap {
    private Logger log = LoggerFactory.getLogger(ProducerMap.class);
    private Map<String, Set<QualifiedType>> map = new HashMap<>();

    Set<QualifiedType> get(String c) {
        Set<QualifiedType> tmp = map.get(c);
        if (tmp == null) {
            return Collections.EMPTY_SET;
        } else {
            return tmp;
        }
    }

    void addToProducerMap(Class c, QualifiedType q) {
        log.trace("adding to ProducerMap: {}/{} ", c.getCanonicalName(), q);
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
        Class tmpC = c;
        if (c.isInterface()) {
            addInterfaceToProducerMap(c, q);
        } else {
            while (tmpC != null && !tmpC.equals(Object.class)) {
                addToProducerMap(tmpC, q);
                tmpC = tmpC.getSuperclass();
            }
            Class[] interfaces = c.getInterfaces();
            for (Class iface : interfaces) {
                addInterfaceToProducerMap(iface, q);
            }
        }
    }

}
