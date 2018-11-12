package com.oneandone.ejbcdiunit2.closure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author aschoerk
 */
public class ProducerMap {
    private Map<Class<?>, Set<QualifiedType>> map = new HashMap<>();

    Set<QualifiedType> get(Class<?> c) {
        return map.get(c);
    }

    void addToProducerMap(Class c, QualifiedType q) {
        Set<QualifiedType> existing = map.get(c);
        if (existing == null) {
            existing = new HashSet<>();
            map.put(c, existing);
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
