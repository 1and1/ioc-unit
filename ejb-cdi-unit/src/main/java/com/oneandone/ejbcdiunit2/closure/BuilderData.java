package com.oneandone.ejbcdiunit2.closure;

import com.oneandone.ejbcdiunit.cfganalyzer.ClasspathHandler;
import com.oneandone.ejbcdiunit2.closure.annotations.SutClasses;
import com.oneandone.ejbcdiunit2.closure.annotations.TestClasses;

import javax.enterprise.inject.Alternative;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BuilderData {

    Set<Class<?>> beansToBeStarted = new HashSet<>(); // these beans must be given to CDI to be started
    Set<Class<?>> beansAvailable = new HashSet<>(); // beans can be used for injects
    Set<Class<?>> enabledAlternatives = new HashSet<>();
    Set<Class<?>> excludedClasses = new HashSet<>();

    ProducerMap producerMap;
    Set<Class<?>> testClassesToBeEvaluated = new HashSet<>();
    Set<Class<?>> testClasses = new HashSet<>();
    Set<Class<?>> sutClasses = new HashSet<>();
    Set<Class<?>> sutClassesToBeEvaluated = new HashSet<>();
    Set<Class<?>> testClassesAvailable = new HashSet<>();
    Set<Class<?>> sutClassesAvailable = new HashSet<>();
    Set<Class<?>> foundAlternativeStereotypes = new HashSet<>();
    Set<Class<?>> foundAlternativeClasses = new HashSet<>();

    public BuilderData(final ProducerMap producerMap) {
        this.producerMap = producerMap;
    }

    public void init(InitialConfiguration cfg) {
        Set<Class<?>> tmp = new HashSet<>();
        if (cfg.testClass != null)
            tmp.add(cfg.testClass);
        tmp.addAll(cfg.initialClasses);
        tmp.addAll(cfg.enabledAlternatives);
        addEnabledAlternatives(cfg.enabledAlternatives);
        try {
            if (cfg.suTClasspath != null)
                addSutClasspaths(cfg.suTClasspath);
            if (cfg.suTPackages != null)
                addSutPackages(cfg.suTPackages);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        if (cfg.excludedClasses != null)
            addExcludedClasses(cfg.excludedClasses);
    }

    void addTestClasses(TestClasses testClassesP) {
        addClasses(Arrays.asList(testClassesP.value()), this.testClasses, testClassesToBeEvaluated);
    }

    void addSutClasses(SutClasses sutClassesP) {
        addClasses(Arrays.asList(sutClassesP.value()), this.sutClasses, sutClassesToBeEvaluated);
    }

    void addClasses(Iterable<Class<?>> value, Set<Class<?>> classes, Set<Class<?>> classesToBeEvaluated) {
        for (Class<?> testClass : value) {
            if (!classes.contains(testClass)) {
                classesToBeEvaluated.add(testClass);
                classes.add(testClass);
                addToClassMap(testClass);
            }
        }
    }

    void addSutPackages(Iterable<Class<?>> sutPackages) throws MalformedURLException {
        for (Class<?> packageClass : sutPackages) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addPackage(packageClass, tmpClasses);
            for (Class clazz : tmpClasses) {
                addToClassMap(clazz);
                sutClassesAvailable.add(clazz);
            }
        }
    }

    void addSutClasspaths(Iterable<Class<?>> sutClasspaths) throws MalformedURLException {
        for (Class<?> classpathClass : sutClasspaths) {
            Set<Class<?>> tmpClasses = new HashSet<>();
            ClasspathHandler.addClassPath(classpathClass, tmpClasses);
            for (Class clazz : tmpClasses) {
                addToClassMap(clazz);
                sutClassesAvailable.add(clazz);
            }
        }
    }

    void addEnabledAlternatives(Iterable<Class<?>> enabledAlternativesP) {
        for (Class<?> alternative : enabledAlternativesP) {
            this.enabledAlternatives.add(alternative);
            if (alternative.getAnnotation(Alternative.class) == null) {
                boolean found = false;
                for (Method m : alternative.getDeclaredMethods()) {
                    if (m.getAnnotation(Alternative.class) != null) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (Field f : alternative.getDeclaredFields()) {
                        if (f.getAnnotation(Alternative.class) != null) {
                            found = true;
                            break;
                        }
                    }

                }
                if (!found) {
                    foundAlternativeClasses.add(alternative);
                } else {
                    testClasses.add(alternative);
                }
            } else {
                testClasses.add(alternative);
            }
            addToClassMap(alternative);
        }
    }

    void addExcludedClasses(Iterable<Class<?>> excludedClassesL) {
        for (Class<?> excl : excludedClassesL) {
            this.excludedClasses.add(excl);
        }
    }



    void addToClassMap(Class<?> clazz) {
        producerMap.addToProducerMap(new QualifiedType(clazz));
    }

}
