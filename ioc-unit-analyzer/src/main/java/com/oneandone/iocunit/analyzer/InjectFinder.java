package com.oneandone.iocunit.analyzer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.reflect.IocUnitTypeUtils;


/**
 * @author aschoerk
 */
public class InjectFinder {

    private final Configuration configuration;
    TesterExtensionsConfigsFinder testerExtensionsConfigsFinder;

    public InjectFinder(Configuration configuration) {
        this.testerExtensionsConfigsFinder = configuration.testerExtensionsConfigsFinder;
        toIgnore.addAll(testerExtensionsConfigsFinder.excludeAsInjects);
        this.configuration = configuration;
    }

    List<Class<?>> toIgnore = new ArrayList<Class<?>>() {
        private static final long serialVersionUID = 1929608071838061220L;

        {
            add(BeanManager.class);
            add(Extension.class);
            add(InjectionPoint.class);
        }
    };

    Set<QualifiedType> injectedTypes = new HashSet<>();

    Set<QualifiedType> getInjectedTypes() {
        return injectedTypes;
    }

    Stack<Class> classes = new Stack<>();
    boolean used = false;

    boolean notIgnoreAble(Type c) {
        for (Class<?> clazz : toIgnore) {
            if (IocUnitTypeUtils.isAssignable(c, clazz))
                return false;
        }
        return true;
    }

    private void findInjects(final Class<?> c, boolean isSuperclass) {
        if (c == null || c.equals(Object.class) || testerExtensionsConfigsFinder.excludeFromInjectScan.contains(c)) {
            return;
        }
        try {
            for (Field f : c.getDeclaredFields()) {
                if(notIgnoreAble(f.getGenericType())) {
                    Set<? extends Annotation> annotations = configuration.injectAnnotations
                            .stream()
                            .map(annotation -> f.getAnnotation(annotation))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    if(!annotations.isEmpty())
                        injectedTypes.add(new QualifiedType(f, annotations));
                }
            }
            if(!isSuperclass) {
                boolean injectedConstructorFound = false;
                for (Constructor constructor : c.getDeclaredConstructors()) {
                    Set<? extends Annotation> annotations = configuration.injectAnnotations
                            .stream()
                            .map(annotation -> constructor.getAnnotation(annotation))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    if(constructor.getAnnotation(Inject.class) != null) {
                        if(injectedConstructorFound) {
                            throw new RuntimeException("Only one Constructor may be injected" + c.getName());
                        }
                        for (Parameter p : constructor.getParameters()) {
                            if(notIgnoreAble(p.getParameterizedType()))
                                injectedTypes.add(new QualifiedType(p, constructor));
                        }
                        injectedConstructorFound = true;
                    }
                }
                for (Method m : c.getDeclaredMethods()) {
                    boolean done = false;
                    if(m.getAnnotation(Inject.class) != null) {
                        for (Parameter p : m.getParameters()) {
                            if(notIgnoreAble(p.getParameterizedType()))
                                injectedTypes.add(new QualifiedType(p, m));
                        }
                        done = true;
                    }
                    if(m.getAnnotation(Produces.class) != null) {
                        if(done) {
                            throw new RuntimeException(m.getName() + " has Inject and Produces");
                        }
                        done = true;
                        for (Parameter p : m.getParameters()) {
                            if(p.getAnnotation(Disposes.class) != null) {
                                throw new RuntimeException(m.getName() + " producer method has Disposes parameter");
                            }
                            if(notIgnoreAble(p.getParameterizedType()))
                                injectedTypes.add(new QualifiedType(p, m));
                        }
                    }
                }
            }
            else { // in superclass only search for injected initializers
                for (Method m : c.getDeclaredMethods()) {
                    boolean done = false;
                    if(m.getAnnotation(Inject.class) != null) {
                        boolean foundInSubclass = false;
                        for (Class subclass : classes) {
                            for (Method subm : subclass.getDeclaredMethods()) {
                                if(subm.getName().equals(m.getName()) && subm.getParameterCount() == m.getParameterCount()) {
                                    foundInSubclass = true;
                                    for (int i = 0; i < subm.getParameterCount(); i++) {
                                        if(!subm.getParameterTypes()[i].equals(m.getParameterTypes()[i])) {
                                            foundInSubclass = false;
                                        }
                                    }
                                }
                            }
                            if(foundInSubclass) {
                                break;
                            }
                        }
                        if(foundInSubclass) {
                            break;
                        }
                        for (Parameter p : m.getParameters()) {
                            if(notIgnoreAble(p.getParameterizedType()))
                                injectedTypes.add(new QualifiedType(p, m));
                        }
                        done = true;
                    }
                }
            }
            classes.push(c);
            findInjects(c.getSuperclass(), true);
        }
        catch (NoClassDefFoundError e) {
            ;
        }
    }


    public void find(Class c) {
        if (used) {
            throw new RuntimeException("InjectFinder can only be used once");
        }
        used = true;
        findInjects(c, false);
    }
}
