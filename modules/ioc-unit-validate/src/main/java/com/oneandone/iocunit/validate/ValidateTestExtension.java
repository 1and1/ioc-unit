package com.oneandone.iocunit.validate;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class ValidateTestExtension implements Extension {

    private static AnnotationLiteral<ApplicationScoped> createApplicationScopedAnnotation() {
        return new AnnotationLiteral<ApplicationScoped>() {
            private static final long serialVersionUID = 1L;
        };
    }

    public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType);

        final Class aClass = annotatedType.getJavaClass();
        if(TestExtensionServices.testExtensionServiceData.get().contains(aClass)) {
            if (ValidationClassFinder.getMethodValidatedAnnotation() != null) {
                builder.addToClass(new AnnotationLiteral() {
                    private static final long serialVersionUID = 4280858811908223334L;

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return ValidationClassFinder.getMethodValidatedAnnotation();
                    }
                });
                pat.setAnnotatedType(builder.create());
            }
        }
    }
}
