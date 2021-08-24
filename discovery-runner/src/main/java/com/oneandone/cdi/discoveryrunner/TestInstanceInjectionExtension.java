/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.cdi.discoveryrunner;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Singleton;

import org.jboss.weld.util.annotated.ForwardingAnnotatedType;


/**
 * Extension class that ensure proper injection of an externally provided
 * test instance.
 * <p>
 * NOTE: When JUnit 5 provides a test instance creation extension point this
 * should be removed and the test instance should be obtained from the
 * container via proper CDI methods.
 * https://github.com/junit-team/junit5/issues/672
 */
public class TestInstanceInjectionExtension implements Extension {

    private static final AnnotationLiteral<Singleton> SINGLETON_LITERAL = new AnnotationLiteral<Singleton>() {
    };

    private static final class AnnotationRewritingAnnotatedType<T> extends ForwardingAnnotatedType<T> {

        private AnnotatedType<T> delegate;
        private Set<Annotation> annotations;

        public AnnotationRewritingAnnotatedType(AnnotatedType<T> delegate, Set<Annotation> annotations) {
            this.delegate = delegate;
            this.annotations = annotations;
        }

        @Override
        public AnnotatedType<T> delegate() {
            return delegate;
        }

        @Override
        public Set<Annotation> getAnnotations() {
            return annotations;
        }

    }

    private Class<?> testClass;
    private Object testInstance;

    TestInstanceInjectionExtension(Class<?> testClass, Object testInstance) {
        this.testClass = testClass;
        this.testInstance = testInstance;
    }

    <T> void rewriteTestClassScope(@Observes ProcessAnnotatedType<T> pat, BeanManager beanManager) {

        AnnotatedType<T> annotatedType = pat.getAnnotatedType();

        if(annotatedType.getJavaClass().equals(testClass)) {

            // Replace any test class's scope with @Singleton
            Set<Annotation> annotations = annotatedType.getAnnotations().stream()
                    .filter(annotation -> beanManager.isScope(annotation.annotationType()))
                    .collect(Collectors.toSet());
            annotations.add(SINGLETON_LITERAL);

            pat.setAnnotatedType(new AnnotationRewritingAnnotatedType<>(annotatedType, annotations));
        }
    }

    <T> void rewriteTestClassInjections(@Observes ProcessInjectionTarget<T> pit) {

        if(pit.getAnnotatedType().getJavaClass().equals(testClass)) {

            InjectionTarget<T> wrapped = pit.getInjectionTarget();

            pit.setInjectionTarget(new InjectionTarget<T>() {

                @SuppressWarnings("unchecked")
                @Override
                public T produce(CreationalContext<T> creationalContext) {
                    return (T) testInstance;
                }

                @Override
                public void dispose(T t) {
                    wrapped.dispose(t);
                }

                @Override
                public Set<InjectionPoint> getInjectionPoints() {
                    return wrapped.getInjectionPoints();
                }

                @Override
                public void inject(T t, CreationalContext<T> creationalContext) {
                    wrapped.inject(t, creationalContext);
                }

                @Override
                public void postConstruct(T t) {
                    wrapped.postConstruct(t);
                }

                @Override
                public void preDestroy(T t) {
                    wrapped.preDestroy(t);
                }
            });

        }
    }

}
