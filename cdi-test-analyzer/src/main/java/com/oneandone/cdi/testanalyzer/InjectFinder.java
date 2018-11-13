package com.oneandone.cdi.testanalyzer;

import org.apache.commons.lang3.reflect.TypeUtils;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author aschoerk
 */
public class InjectFinder {

    List<Class<?>> toIgnore = new ArrayList<Class<?>>()
    {{
        add(BeanManager.class);
        add(Extension.class);
    }};

    Set<QualifiedType> injectedTypes = new HashSet<>();

    Set<QualifiedType> getInjectedTypes() {
        return injectedTypes;
    }

    Stack<Class> classes = new Stack<>();
    boolean used = false;

    boolean notIgnoreAble(Type c) {
        for (Class<?> clazz: toIgnore) {
            if (TypeUtils.isAssignable(c,clazz))
                return false;
        }
        return true;
    }

    private void findInjects(final Class<?> c, boolean isSuperclass) {
        if (c == null || c.equals(Object.class)) {
            return;
        }
        for (Field f : c.getDeclaredFields()) {
            if (f.getAnnotation(Inject.class) != null && notIgnoreAble(f.getGenericType())) {
                injectedTypes.add(new QualifiedType(f));
            }
        }
        if (!isSuperclass) {
            boolean injectedConstructorFound = false;
            for (Constructor constructor : c.getDeclaredConstructors()) {
                if (constructor.getAnnotation(Inject.class) != null) {
                    if (injectedConstructorFound) {
                        throw new RuntimeException("Only one Constructor may be injected" + c.getName());
                    }
                    for (Parameter p : constructor.getParameters()) {
                        if (!notIgnoreAble(p.getParameterizedType()))
                            injectedTypes.add(new QualifiedType(p, constructor));
                    }
                    injectedConstructorFound = true;
                }
            }
            for (Method m : c.getDeclaredMethods()) {
                boolean done = false;
                if (m.getAnnotation(Inject.class) != null) {
                    for (Parameter p : m.getParameters()) {
                        if (notIgnoreAble(p.getParameterizedType()))
                            injectedTypes.add(new QualifiedType(p, m));
                    }
                    done = true;
                }
                if (m.getAnnotation(Produces.class) != null) {
                    if (done) {
                        throw new RuntimeException(m.getName() + " has Inject and Produces");
                    }
                    done = true;
                    for (Parameter p : m.getParameters()) {
                        if (p.getAnnotation(Disposes.class) != null) {
                            throw new RuntimeException(m.getName() + " producer method has Disposes parameter");
                        }
                        if (notIgnoreAble(p.getParameterizedType()))
                            injectedTypes.add(new QualifiedType(p, m));
                    }
                }
            }
        } else { // in superclass only search for injected initializers
            for (Method m : c.getDeclaredMethods()) {
                boolean done = false;
                if (m.getAnnotation(Inject.class) != null) {
                    boolean foundInSubclass = false;
                    for (Class subclass : classes) {
                        for (Method subm : subclass.getDeclaredMethods()) {
                            if (subm.getName().equals(m.getName()) && subm.getParameterCount() == m.getParameterCount()) {
                                foundInSubclass = true;
                                for (int i = 0; i < subm.getParameterCount(); i++) {
                                    if (!subm.getParameterTypes()[i].equals(m.getParameterTypes()[i])) {
                                        foundInSubclass = false;
                                    }
                                }
                            }
                        }
                        if (foundInSubclass) {
                            break;
                        }
                    }
                    if (foundInSubclass) {
                        break;
                    }
                    for (Parameter p : m.getParameters()) {
                        if (notIgnoreAble(p.getParameterizedType()))
                            injectedTypes.add(new QualifiedType(p, m));
                    }
                    done = true;
                }
            }
        }
        classes.push(c);
        findInjects(c.getSuperclass(), true);
    }


    public void find(Class c) {


        if (used) {
            throw new RuntimeException("InjectFinder can only be used once");
        }
        used = true;
        findInjects(c, false);
    }
}
