package com.oneandone.cdi.weld;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.deltaspike.core.api.literal.ApplicationScopedLiteral;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

public class TestScopeExtension implements Extension {

    private static final ApplicationScopedLiteral APPLICATIONSCOPED = new ApplicationScopedLiteral();

    private Class<?> testClass;

    public TestScopeExtension() {}

    public TestScopeExtension(Class<?> testClass) {
        this.testClass = testClass;
    }


    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        if (annotatedType.getJavaClass().equals(testClass)) {
            AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType).addToClass(APPLICATIONSCOPED);
            pat.setAnnotatedType(builder.create());
        }
    }

}