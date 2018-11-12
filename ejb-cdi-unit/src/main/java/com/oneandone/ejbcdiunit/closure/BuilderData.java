package com.oneandone.ejbcdiunit.closure;

import com.oneandone.ejbcdiunit.cfganalyzer.ClasspathHandler;
import com.oneandone.ejbcdiunit.closure.annotations.*;

import javax.enterprise.inject.Alternative;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BuilderData {

    Set<Class<?>> beansToBeStarted = new HashSet<>(); // these beans must be given to CDI to be started
    Set<Class<?>> beansAvailable = new HashSet<>(); // beans can be used for injects
    Set<Class<?>> enabledAlternatives = new HashSet<>();
    Set<Class<?>> excludedClasses = new HashSet<>();

    Set<QualifiedType> injections = new HashSet<>();
    Set<QualifiedType> produces = new HashSet<>();
    // ClassMap<QualifiedType> producerMap = new ClassMap<>();
    Map<Class<?>, Set<QualifiedType>> producerMap = new HashMap<>();
    // Map<Class<?>, Set<Class<?>>> classMap = new HashMap<>();
    Set<Class<?>> testClassesToBeEvaluated = new HashSet<>();
    Set<Class<?>> testClasses = new HashSet<>();
    Set<Class<?>> sutClasses = new HashSet<>();
    Set<Class<?>> sutClassesToBeEvaluated = new HashSet<>();
    Set<Class<?>> testClassesAvailable = new HashSet<>();
    Set<Class<?>> sutClassesAvailable = new HashSet<>();
    Set<Class<?>> foundAlternativeStereotypes;

    void addTestClasses(TestClasses testClassesx) {
        addClasses(testClassesx.value(), testClasses, testClassesToBeEvaluated);
    }

    void addSutClasses(SutClasses sutClassesx) {
        addClasses(sutClassesx.value(), sutClasses, sutClassesToBeEvaluated);
    }

    void addClasses(Class<?>[] value, Set<Class<?>> classes, Set<Class<?>> classesToBeEvaluated) {
        for (Class<?> testClass : value) {
            if (!classes.contains(testClass)) {
                classesToBeEvaluated.add(testClass);
                classes.add(testClass);
                addToClassMap(testClass);
            }
        }
    }

    void addSutPackages(SutPackages sutPackages) throws MalformedURLException {
        for (Class<?> packageClass : sutPackages.value()) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addPackage(packageClass, tmpClasses);
            for (Class clazz : tmpClasses) {
                addToClassMap(clazz);
                sutClassesAvailable.add(clazz);
            }
        }
    }
    void addSutClasspaths(SutClasspaths sutClasspaths) throws MalformedURLException {
        for (Class<?> classpathClass : sutClasspaths.value()) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addClassPath(classpathClass, tmpClasses);
            for (Class clazz : tmpClasses) {
                addToClassMap(clazz);
                sutClassesAvailable.add(clazz);
            }
        }
    }

    void addEnabledAlternatives(EnabledAlternatives enabledAlternatives) {
        for (Class<?> alternative : enabledAlternatives.value()) {
            if (alternative.getAnnotation(Alternative.class) != null) {
                // TODO: warn, should be annotated @Alternative
                this.enabledAlternatives.add(alternative);
            }
            testClasses.add(alternative);
            addToClassMap(alternative);
        }
    }

    void addExcludedClasses(ExcludedClasses excludedClassesL) {
        for (Class<?> excl : excludedClassesL.value()) {
            this.excludedClasses.add(excl);
        }
    }

    void addToProducerMap(Class c, QualifiedType q) {
        Set<QualifiedType> existing = producerMap.get(c);
        if (existing == null) {
            existing = new HashSet<>();
            producerMap.put(c, existing);
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


    void addToClassMap(Class<?> clazz) {
        addToProducerMap(new QualifiedType(clazz));
    }

}
