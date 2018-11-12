package com.oneandone.ejbcdiunit.closure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassMap<T> {
    Map<Class<?>, Set<T>> classMap = new HashMap<>();
    public void add(Class<?> clazz, T content) {
        Class tmpC = clazz;
        while (!tmpC.equals(Object.class)) {
            addToMap(tmpC, content);
            tmpC = tmpC.getSuperclass();
        }
        Class[] interfaces = clazz.getInterfaces();
        for (Class iface : interfaces) {
            addInterfaceToMap(iface, content);
        }

    }

    private void addInterfaceToMap(final Class iface, final T clazz) {
        addToMap(iface, clazz);
        Class[] interfaces = iface.getInterfaces();
        for (Class subiface : interfaces) {
            addInterfaceToMap(subiface, clazz);
        }
    }

    private void addToMap(final Class<?> tmpC, final T clazz) {
        Set<T> entities = classMap.get(tmpC);
        if (entities == null) {
            entities = new HashSet<>();
            classMap.put(tmpC, entities);
        }
        entities.add(clazz);
    }

}
