package com.oneandone.ejbcdiunit.closure;

import com.oneandone.cdiunit.internal.easymock.EasyMockExtension;
import com.oneandone.cdiunit.internal.mockito.MockitoExtension;
import com.oneandone.ejbcdiunit.closure.annotations.*;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Extension;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author aschoerk
 */
class Builder {

    BuilderData data = new BuilderData();


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
        data.injections.addAll(injectFinder.getInjectedTypes());
        return this;
    }

    Builder injectHandled(QualifiedType inject) {
        data.injections.remove(inject);
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
                data.produces.add(q);
                data.addToProducerMap(q);
            }
        }
        return this;
    }

    Builder producerMethods(Class c) {
        for (Method m : c.getDeclaredMethods()) {
            if (containsProducingAnnotation(m.getAnnotations())) {
                final QualifiedType q = new QualifiedType(m);
                data.produces.add(q);
                data.addToProducerMap(q);
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
        TestClasses testClassesx = c.getAnnotation(TestClasses.class);
        if (testClassesx != null) {
            data.addTestClasses(testClassesx);
        }
        return this;
    }




    Builder sutClassAnnotation(Class<?> c) {
        SutClasses sutClassesx = c.getAnnotation(SutClasses.class);
        if (sutClassesx != null) {
            data.addSutClasses(sutClassesx);
        }
        return this;
    }


    Builder sutPackagesAnnotation(Class<?> c) throws MalformedURLException {
        SutPackages sutPackages = c.getAnnotation(SutPackages.class);
        if (sutPackages != null) {
            data.addSutPackages(sutPackages);
        }
        return this;
    }



    Builder sutClasspathsAnnotation(Class<?> c) throws MalformedURLException {
        SutClasspaths sutClasspaths = c.getAnnotation(SutClasspaths.class);
        if (sutClasspaths != null) {
            data.addSutClasspaths(sutClasspaths);
        }
        return this;
    }


    Builder enabledAlternatives(Class<?> c) throws MalformedURLException {
        EnabledAlternatives enabledAlternatives = c.getAnnotation(EnabledAlternatives.class);
        if (enabledAlternatives != null) {
            data.addEnabledAlternatives(enabledAlternatives);
        }
        return this;
    }




    Builder excludes(Class<?> c) throws MalformedURLException {
        ExcludedClasses excludedClassesL = c.getAnnotation(ExcludedClasses.class);
        if (excludedClassesL != null) {
            data.addExcludedClasses(excludedClassesL);
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
