package net.oneandone.ejbcdiunit.relbuilder.code.closure;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Produces;

import com.oneandone.ejbcdiunit.cfganalyzer.ClasspathHandler;

import net.oneandone.ejbcdiunit.relbuilder.code.closure.annotations.EnabledAlternatives;
import net.oneandone.ejbcdiunit.relbuilder.code.closure.annotations.ExcludedClasses;
import net.oneandone.ejbcdiunit.relbuilder.code.closure.annotations.SutClasses;
import net.oneandone.ejbcdiunit.relbuilder.code.closure.annotations.SutClasspaths;
import net.oneandone.ejbcdiunit.relbuilder.code.closure.annotations.SutPackages;
import net.oneandone.ejbcdiunit.relbuilder.code.closure.annotations.TestClasses;

/**
 * @author aschoerk
 */
class Builder {
    Set<Class<?>> beansToBeStarted = new HashSet<>(); // these beans must be given to CDI to be started
    Set<Class<?>> beansAvailable = new HashSet<>(); // beans can be used for injects
    Set<Class<?>> alternatives = new HashSet<>();
    Set<Class<?>> excludedClasses = new HashSet<>();

    Set<QualifiedType> injections = new HashSet<>();
    Set<QualifiedType> produces = new HashSet<>();
    Map<Class<?>, Set<QualifiedType>> producerMap = new HashMap<>();
    Map<Class<?>, Set<Class<?>>> classMap = new HashMap<>();
    Set<Class<?>> testClassesToBeEvaluated = new HashSet<>();
    Set<Class<?>> testClasses = new HashSet<>();
    Set<Class<?>> sutClasses = new HashSet<>();
    Set<Class<?>> sutClassesToBeEvaluated = new HashSet<>();
    Set<Class<?>> testClassesAvailable = new HashSet<>();
    Set<Class<?>> sutClassesAvailable = new HashSet<>();
    Set<Class<?>> extensions = new HashSet<>();
    Set<Class<?>> elseClasses = new HashSet<>();

    private void findInnerClasses(final Class c, final Set<Class<?>> staticInnerClasses) {
        for (Class innerClass : c.getDeclaredClasses()) {
            if (Modifier.isStatic(innerClass.getModifiers()) && CdiConfigBuilder.mightBeBean(innerClass)) {
                staticInnerClasses.add(innerClass);
                findInnerClasses(innerClass, staticInnerClasses);
            }
        }
    }

    boolean isTestClass(Class<?> c) {
        return testClasses.contains(c) || testClassesAvailable.contains(c);
    }

    boolean isSuTClass(Class<?> c) {
        return sutClasses.contains(c) || sutClassesAvailable.contains(c);
    }

    Set<Class<?>> findInClassMap(Class<?> c) {
        return classMap.get(c);
    }

    Set<QualifiedType> findInProducerMap(Class<?> c) {
        return producerMap.get(c);
    }

    void moveToBeEvaluatedTo(Set<Class<?>> newToBeEvaluated) {
        newToBeEvaluated.clear();
        newToBeEvaluated.addAll(testClassesToBeEvaluated);
        testClassesToBeEvaluated.clear();
        newToBeEvaluated.addAll(sutClassesToBeEvaluated);
        sutClassesToBeEvaluated.clear();
    }


    Builder tobeStarted(Class c) {
        beansToBeStarted.add(c);
        return this;
    }

    Builder setAvailable(Class c) {
        beansAvailable.add(c);
        return this;
    }

    Builder innerClasses(Class c) {
        findInnerClasses(c, beansAvailable);
        return this;
    }

    Builder injects(Class c) {
        InjectFinder injectFinder = new InjectFinder();
        injectFinder.find(c);
        injections.addAll(injectFinder.getInjectedTypes());
        return this;
    }


    Builder producerFields(Class c) {
        for (Field f : c.getDeclaredFields()) {
            if (f.getAnnotation(Produces.class) != null) {
                final QualifiedType q = new QualifiedType(f);
                produces.add(q);
                addToProducerMap(q);
            }
        }
        return this;
    }

    Builder producerMethods(Class c) {
        for (Method m : c.getDeclaredMethods()) {
            if (m.getAnnotation(Produces.class) != null) {
                final QualifiedType q = new QualifiedType(m);
                produces.add(q);
                addToProducerMap(q);
            }
        }
        return this;
    }

    Builder testClasses(Class<?> c) {
        TestClasses testClassesx = c.getAnnotation(TestClasses.class);
        if (testClassesx != null) {
            for (Class<?> testClass : testClassesx.value()) {
                if (!testClasses.contains(testClass)) {
                    testClassesToBeEvaluated.add(testClass);
                    testClasses.add(testClass);
                }
            }
        }
        return this;
    }

    Builder sutClasses(Class<?> c) {
        SutClasses sutClassesx = c.getAnnotation(SutClasses.class);
        if (sutClasses != null) {
            for (Class<?> sutClass : sutClassesx.value()) {
                if (!sutClasses.contains(sutClass)) {
                    sutClassesToBeEvaluated.add(sutClass);
                    sutClasses.add(sutClass);
                }
            }
        }
        return this;
    }

    Builder sutPackages(Class<?> c) throws MalformedURLException {
        SutPackages sutPackages = c.getAnnotation(SutPackages.class);
        if (sutPackages != null) {
            for (Class<?> packageClass : sutPackages.value()) {
                ClasspathHandler.addPackage(packageClass, sutClassesAvailable);
            }
        }
        return this;
    }

    Builder sutClasspaths(Class<?> c) throws MalformedURLException {
        SutClasspaths sutClasspaths = c.getAnnotation(SutClasspaths.class);
        if (sutClasspaths != null) {
            for (Class<?> classpathClass : sutClasspaths.value()) {
                ClasspathHandler.addClassPath(classpathClass, sutClassesAvailable);
            }
        }
        return this;
    }

    Builder alternatives(Class<?> c) throws MalformedURLException {
        EnabledAlternatives enabledAlternatives = c.getAnnotation(EnabledAlternatives.class);
        if (enabledAlternatives != null) {
            for (Class<?> alternative : enabledAlternatives.value()) {
                alternatives.add(alternative);
            }
        }
        return this;
    }

    Builder excludes(Class<?> c) throws MalformedURLException {
        ExcludedClasses excludedClassesL = c.getAnnotation(ExcludedClasses.class);
        if (excludedClassesL != null) {
            for (Class<?> excl : excludedClassesL.value()) {
                this.excludedClasses.add(excl);
            }
        }
        return this;
    }

    Builder elseClass(Class<?> c) {
        if (CdiConfigBuilder.isExtension(c)) {
            extensions.add(c);
        } else {
            elseClasses.add(c);
        }
        return this;
    }

    private void addToProducerMap(Class c, QualifiedType q) {
        Set<QualifiedType> existing = producerMap.get(c);
        if (existing == null) {
            existing = new HashSet<>();
            producerMap.put(c, existing);
        }
        existing.add(q);
    }

    private void addInterfaceToProducerMap(Class iface, QualifiedType q) {
        addToProducerMap(iface, q);
        Class[] interfaces = iface.getInterfaces();
        for (Class subiface : interfaces) {
            addInterfaceToProducerMap(subiface, q);
        }
    }


    private void addToProducerMap(QualifiedType q) {
        Class c = q.getRawtype();
        Class tmpC = c;
        while (!tmpC.equals(Object.class)) {
            addToProducerMap(tmpC, q);
            tmpC = tmpC.getSuperclass();
        }
        Class[] interfaces = c.getInterfaces();
        for (Class iface : interfaces) {
            addInterfaceToProducerMap(iface, q);
        }
    }


    private void addToClassMap(Class<?> clazz) {
        Class tmpC = clazz;
        while (!tmpC.equals(Object.class)) {
            addToClassMap(tmpC, clazz);
            tmpC = tmpC.getSuperclass();
        }
        Class[] interfaces = clazz.getInterfaces();
        for (Class iface : interfaces) {
            addInterfaceToClassMap(iface, clazz);
        }

    }

    private void addInterfaceToClassMap(final Class iface, final Class<?> clazz) {
        addToClassMap(iface, clazz);
        Class[] interfaces = iface.getInterfaces();
        for (Class subiface : interfaces) {
            addInterfaceToClassMap(subiface, clazz);
        }
    }

    private void addToClassMap(final Class<?> tmpC, final Class<?> clazz) {
        Set<Class<?>> entities = classMap.get(tmpC);
        if (entities == null) {
            entities = new HashSet<>();
            classMap.put(tmpC, entities);
        }
        entities.add(clazz);
    }

}
