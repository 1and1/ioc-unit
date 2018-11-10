package com.oneandone.ejbcdiunit.closure;

import com.oneandone.cdiunit.internal.easymock.EasyMockExtension;
import com.oneandone.cdiunit.internal.mockito.MockitoExtension;
import com.oneandone.ejbcdiunit.cfganalyzer.ClasspathHandler;
import com.oneandone.ejbcdiunit.closure.annotations.*;

import javax.enterprise.inject.spi.Extension;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public Set<Class<? extends Extension>> getExtensions() {
        return extensions;
    }

    Set<Class<? extends Extension>> extensions = new HashSet<>();
    Set<Class<?>> elseClasses = new HashSet<>();
    Set<QualifiedType> handledInjections = new HashSet<>();

    private void findInnerClasses(final Class c, final Set<Class<?>> staticInnerClasses) {
        for (Class innerClass : c.getDeclaredClasses()) {
            if (Modifier.isStatic(innerClass.getModifiers()) && CdiConfigBuilder.mightBeBean(innerClass)) {
                staticInnerClasses.add(innerClass);
                addToClassMap(innerClass);
                findInnerClasses(innerClass, staticInnerClasses);
            }
        }
    }

    boolean isTestClass(Class<?> c) {
        if (testClasses.contains(c)) {
            return true;
        } else
            return false;
    }

    boolean isTestClassAvailable(Class<?> c) {
        if (testClassesAvailable.contains(c)) {
            return true;
        } else if (c.getDeclaringClass() != null)
            return isTestClass(c.getDeclaringClass());
        else
            return false;
    }

    boolean isSuTClass(Class<?> c) {
        if (sutClasses.contains(c) || sutClassesAvailable.contains(c)) {
            return true;
        } else if (c.getDeclaringClass() != null)
            return isSuTClass(c.getDeclaringClass());
        else
            return false;
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
        addToClassMap(c);
        return this;
    }

    Builder setAvailable(Class c) {
        beansAvailable.add(c);
        addToClassMap(c);
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

    Builder injectHandled(QualifiedType inject) {
        injections.remove(inject);
        handledInjections.add(inject);
        return this;
    }

    boolean containsProducingAnnotation(final Annotation[] annotations) {
        for (Annotation ann : annotations) {
            final String name = ann.annotationType().getName();
            if (name.equals("org.easymock.Mock")) {
                extensions.add(EasyMockExtension.class);
                return true;
            } else if (name.equals("org.mockito.Mock")) {
                extensions.add(MockitoExtension.class);
                return true;
            } else if(name.equals("javax.enterprise.inject.Produces")) {
                return true;
            }
        }
        return false;
    }


    Builder producerFields(Class c) {
        for (Field f : c.getDeclaredFields()) {
            if (containsProducingAnnotation(f.getAnnotations())) {
                final QualifiedType q = new QualifiedType(f);
                produces.add(q);
                addToProducerMap(q);
            }
        }
        return this;
    }

    Builder producerMethods(Class c) {
        for (Method m : c.getDeclaredMethods()) {
            if (containsProducingAnnotation(m.getAnnotations())) {
                final QualifiedType q = new QualifiedType(m);
                produces.add(q);
                addToProducerMap(q);
            }
        }
        return this;
    }

    Builder testClass(Class<?> c) {
        testClasses.add(c);
        testClassesAvailable.remove(c);
        addToClassMap(c);
        return this;
    }

    Builder sutClass(Class<?> c) {
        sutClasses.add(c);
        sutClassesAvailable.remove(c);
        addToClassMap(c);
        return this;
    }

    Builder testClassAnnotation(Class<?> c) {
        TestClasses testClassesx = c.getAnnotation(TestClasses.class);
        if (testClassesx != null) {
            for (Class<?> testClass : testClassesx.value()) {
                if (!testClasses.contains(testClass)) {
                    testClassesToBeEvaluated.add(testClass);
                    testClasses.add(testClass);
                    addToClassMap(testClass);
                }
            }
        }
        return this;
    }

    Builder sutClassAnnotation(Class<?> c) {
        SutClasses sutClassesx = c.getAnnotation(SutClasses.class);
        if (sutClassesx != null) {
            for (Class<?> sutClass : sutClassesx.value()) {
                if (!sutClasses.contains(sutClass)) {
                    sutClassesToBeEvaluated.add(sutClass);
                    sutClasses.add(sutClass);
                    addToClassMap(sutClass);
                }
            }
        }
        return this;
    }

    Builder sutPackagesAnnotation(Class<?> c) throws MalformedURLException {
        SutPackages sutPackages = c.getAnnotation(SutPackages.class);
        if (sutPackages != null) {
            for (Class<?> packageClass : sutPackages.value()) {
                Set<Class<?>> tmpClasses = new HashSet<>();
                ClasspathHandler.addPackage(packageClass, tmpClasses);
                for (Class clazz : tmpClasses) {
                    addToClassMap(clazz);
                    sutClassesAvailable.add(clazz);
                }
            }
        }
        return this;
    }

    Builder sutClasspathsAnnotation(Class<?> c) throws MalformedURLException {
        SutClasspaths sutClasspaths = c.getAnnotation(SutClasspaths.class);
        if (sutClasspaths != null) {
            for (Class<?> classpathClass : sutClasspaths.value()) {
                Set<Class<?>> tmpClasses = new HashSet<>();
                ClasspathHandler.addClassPath(classpathClass, tmpClasses);
                for (Class clazz : tmpClasses) {
                    addToClassMap(clazz);
                    sutClassesAvailable.add(clazz);
                }
            }
        }
        return this;
    }

    Builder alternatives(Class<?> c) throws MalformedURLException {
        EnabledAlternatives enabledAlternatives = c.getAnnotation(EnabledAlternatives.class);
        if (enabledAlternatives != null) {
            for (Class<?> alternative : enabledAlternatives.value()) {
                alternatives.add(alternative);
                testClasses.add(alternative);
                addToClassMap(alternative);
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
            extensions.add((Class<? extends Extension>) c);
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
        while (tmpC != null && !tmpC.equals(Object.class)) {
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

    public Builder producerCandidates() {
        Set<Class<?>> tmp = new HashSet<>();
        tmp.addAll(this.beansAvailable);
        tmp.removeAll(this.beansToBeStarted);
        Builder result = new Builder();
        for (Class<?> c: tmp) {
            result.setAvailable(c);  // necessary? already is available
            result.producerFields(c);
            result.producerMethods(c);
        }
        return result;
    }

    public enum ClassKind {
        TEST,
        TEST_AVAILABLE,
        SUT_TOSTART,
        SUT_AVAILABLE
    }

    public ClassKind getClassKind(Class<?> c) {
        if (testClasses.contains(c))
            return ClassKind.TEST;
        if (testClassesAvailable.contains(c)) {
            return ClassKind.TEST_AVAILABLE;
        }
        if (sutClasses.contains(c)) {
            return ClassKind.SUT_TOSTART;
        }
        if (sutClassesAvailable.contains(c)) {
            return ClassKind.SUT_AVAILABLE;
        }
        throw new RuntimeException("expected test, testavailable, sut or sutavailable");
    }

}
