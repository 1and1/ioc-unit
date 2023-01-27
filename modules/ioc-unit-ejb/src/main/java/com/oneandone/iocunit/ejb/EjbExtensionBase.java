package com.oneandone.iocunit.ejb;

import java.lang.annotation.Annotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.util.AnnotationLiteral;

/**
 * @author aschoerk
 */
public class EjbExtensionBase {
    protected static AnnotationLiteral<Default> createDefaultAnnotation() {
        return new AnnotationLiteral<Default>() {
            private static final long serialVersionUID = 1L;
        };
    }

    protected static AnnotationLiteral<Dependent> createDependentAnnotation() {
        return new AnnotationLiteral<Dependent>() {
            private static final long serialVersionUID = 1L;
        };
    }

    protected static AnnotationLiteral<ApplicationScoped> createApplicationScopedAnnotation() {
        return new AnnotationLiteral<ApplicationScoped>() {
            private static final long serialVersionUID = 1L;
        };
    }

    <T extends Annotation> T findAnnotation(Class<?> annotatedType, Class<T> annotation) {
        if (annotatedType.equals(Object.class)) {
            return null;
        }
        return annotatedType.getAnnotation(annotation);
    }

    <T extends Annotation> boolean isAnnotationPresent(Class<?> annotatedType,  Class<T> annotation) {
        if (annotatedType.equals(Object.class)) {
            return false;
        }
        return annotatedType.isAnnotationPresent(annotation);
    }

    <T extends Annotation, X> boolean isAnnotationPresent(ProcessAnnotatedType<X> pat, Class<T> annotation) {
        return isAnnotationPresent(pat.getAnnotatedType().getJavaClass(), annotation);
    }




}
