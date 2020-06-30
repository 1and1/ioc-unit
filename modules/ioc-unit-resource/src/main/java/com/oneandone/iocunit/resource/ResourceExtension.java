package com.oneandone.iocunit.resource;

import javax.annotation.Resource;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

/**
 * @author aschoerk
 */
public class ResourceExtension implements Extension {
    public <T> void processAnnotatedType(@Observes @WithAnnotations(Resource.class)
                                                 ProcessAnnotatedType<T> pat) {
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType);

        final Class aClass = annotatedType.getJavaClass();
        boolean modified = false;
        for (AnnotatedField<? super T> field : annotatedType.getFields()) {
            Resource resource = field.getAnnotation(Resource.class);
            if(resource != null) {  // all Resources will be set injected. The Tester must provide anything for them.
                modified = true;
                builder.addToField(field, new AnnotationLiteral<Inject>() {
                    private static final long serialVersionUID = 1L;
                });
                Produces produces = field.getAnnotation(Produces.class);
                doResourceQualifyIfNecessary(builder, field, resource);
                // cannot produce, since the container is not there. Must be injected by test-code
                if (produces != null) {
                    builder.removeFromField(field, Produces.class);
                }
            }

        }
        if (modified) {
            pat.setAnnotatedType(builder.create());
        }
    }

    private <T> void doResourceQualifyIfNecessary(final AnnotatedTypeBuilder<T> builder, final AnnotatedField<? super T> field, final Resource resource) {
        if (field.getAnnotation(Produces.class) == null) {
            if(resource != null && !(resource.name().isEmpty() && resource.mappedName().isEmpty() && resource.lookup().isEmpty())) {
                builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(resource.name(), resource.lookup(), resource.mappedName()));
            }
        }
    }
}
