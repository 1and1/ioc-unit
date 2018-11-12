package com.oneandone.ejbcdiunit.closure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Extension;

import com.oneandone.cdiunit.internal.easymock.EasyMockExtension;
import com.oneandone.cdiunit.internal.mockito.MockitoExtension;
import com.oneandone.ejbcdiunit.closure.annotations.EnabledAlternatives;
import com.oneandone.ejbcdiunit.closure.annotations.ExcludedClasses;
import com.oneandone.ejbcdiunit.closure.annotations.SutClasses;
import com.oneandone.ejbcdiunit.closure.annotations.SutClasspaths;
import com.oneandone.ejbcdiunit.closure.annotations.SutPackages;
import com.oneandone.ejbcdiunit.closure.annotations.TestClasses;

/**
 * @author aschoerk
 */
class Builder {

    Set<QualifiedType> injections = new HashSet<>();
    Set<QualifiedType> produces = new HashSet<>();
    ProducerMap producerMap = new ProducerMap();
    BuilderData data = new BuilderData(producerMap);

    public Builder(final InitialConfiguration cfg) {
        init(cfg);
    }


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
                data.addToClassMap(innerClass);
                findInnerClasses(innerClass, staticInnerClasses);
            }
        }
    }

    boolean isTestClass(Class<?> c) {
        if (data.testClasses.contains(c)) {
            return true;
        } else
            return false;
    }

    boolean isTestClassAvailable(Class<?> c) {
        if (data.testClassesAvailable.contains(c)) {
            return true;
        } else if (c.getDeclaringClass() != null)
            return isTestClass(c.getDeclaringClass());
        else
            return false;
    }

    boolean isSuTClass(Class<?> c) {
        if (data.sutClasses.contains(c) || data.sutClassesAvailable.contains(c)) {
            return true;
        } else if (c.getDeclaringClass() != null)
            return isSuTClass(c.getDeclaringClass());
        else
            return false;
    }

    void moveToBeEvaluatedTo(Set<Class<?>> newToBeEvaluated) {
        newToBeEvaluated.clear();
        newToBeEvaluated.addAll(data.testClassesToBeEvaluated);
        data.testClassesToBeEvaluated.clear();
        newToBeEvaluated.addAll(data.sutClassesToBeEvaluated);
        data.sutClassesToBeEvaluated.clear();
    }


    Builder tobeStarted(Class c) {
        data.beansToBeStarted.add(c);
        data.addToClassMap(c);
        return this;
    }

    Builder setAvailable(Class c) {
        data.beansAvailable.add(c);
        data.addToClassMap(c);
        return this;
    }

    Builder innerClasses(Class c) {
        findInnerClasses(c, data.beansAvailable);
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
                producerMap.addToProducerMap(q);
            }
        }
        return this;
    }

    Builder producerMethods(Class c) {
        for (Method m : c.getDeclaredMethods()) {
            if (containsProducingAnnotation(m.getAnnotations())) {
                final QualifiedType q = new QualifiedType(m);
                produces.add(q);
                producerMap.addToProducerMap(q);
            }
        }
        return this;
    }

    Builder testClass(Class<?> c) {
        data.testClasses.add(c);
        data.testClassesAvailable.remove(c);
        data.addToClassMap(c);
        return this;
    }

    Builder sutClass(Class<?> c) {
        data.sutClasses.add(c);
        data.sutClassesAvailable.remove(c);
        data.addToClassMap(c);
        return this;
    }

    Builder testClassAnnotation(Class<?> c) {
        TestClasses testClasses = c.getAnnotation(TestClasses.class);
        if (testClasses != null) {
            data.addTestClasses(testClasses);
        }
        return this;
    }


    Builder sutClassAnnotation(Class<?> c) {
        SutClasses sutClasses = c.getAnnotation(SutClasses.class);
        if (sutClasses != null) {
            data.addSutClasses(sutClasses);
        }
        return this;
    }


    Builder sutPackagesAnnotation(Class<?> c) throws MalformedURLException {
        SutPackages sutPackages = c.getAnnotation(SutPackages.class);
        if (sutPackages != null) {
            data.addSutPackages(Arrays.asList(sutPackages.value()));
        }
        return this;
    }



    Builder sutClasspathsAnnotation(Class<?> c) throws MalformedURLException {
        SutClasspaths sutClasspaths = c.getAnnotation(SutClasspaths.class);
        if (sutClasspaths != null) {
            data.addSutClasspaths(Arrays.asList(sutClasspaths.value()));
        }
        return this;
    }


    Builder enabledAlternatives(Class<?> c) throws MalformedURLException {
        EnabledAlternatives enabledAlternatives = c.getAnnotation(EnabledAlternatives.class);
        if (enabledAlternatives != null) {
            data.addEnabledAlternatives(Arrays.asList(enabledAlternatives.value()));
        }
        return this;
    }




    Builder excludes(Class<?> c) throws MalformedURLException {
        ExcludedClasses excludedClassesL = c.getAnnotation(ExcludedClasses.class);
        if (excludedClassesL != null) {
            data.addExcludedClasses(Arrays.asList(excludedClassesL.value()));
        }
        return this;
    }



    Builder elseClass(Class<?> c) {
        if (CdiConfigBuilder.isExtension(c)) {
            extensions.add((Class<? extends Extension>) c);
        } else if (c.isAnnotation()) {
            if (c.isAnnotationPresent(Stereotype.class) && c.isAnnotationPresent(Alternative.class)) {
                data.foundAlternativeStereotypes.add(c);
            } else {
                elseClasses.add(c);
            }
        } else {
                elseClasses.add(c);
        }

        return this;
    }


    public Builder producerCandidates() {
        Set<Class<?>> tmp = new HashSet<>();
        tmp.addAll(data.beansAvailable);
        tmp.removeAll(data.beansToBeStarted);
        Builder result = new Builder(new InitialConfiguration());
        for (Class<?> c: tmp) {
            result.setAvailable(c);  // necessary? already is available
            result.producerFields(c);
            result.producerMethods(c);
        }
        return result;
    }

    private void init(final InitialConfiguration cfg) {
        this.producerMap = new ProducerMap();
        data = new BuilderData(this.producerMap);
        data.init(cfg);
    }

    public enum ClassKind {
        TEST,
        TEST_AVAILABLE,
        SUT_TOSTART,
        SUT_AVAILABLE
    }

    public ClassKind getClassKind(Class<?> c) {
        if (data.testClasses.contains(c))
            return ClassKind.TEST;
        if (data.testClassesAvailable.contains(c)) {
            return ClassKind.TEST_AVAILABLE;
        }
        if (data.sutClasses.contains(c)) {
            return ClassKind.SUT_TOSTART;
        }
        if (data.sutClassesAvailable.contains(c)) {
            return ClassKind.SUT_AVAILABLE;
        }
        throw new RuntimeException("expected test, testavailable, sut or sutavailable");
    }

}
