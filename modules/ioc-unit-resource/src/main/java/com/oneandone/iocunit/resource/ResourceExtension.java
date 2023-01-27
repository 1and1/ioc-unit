package com.oneandone.iocunit.resource;

import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

/**
 * @author aschoerk
 */
public class ResourceExtension implements Extension {

    static Set<String> resourceToIgnore = new HashSet<String>() {
        private static final long serialVersionUID = -1540511962019840039L;

        {
            add("javax.jms.Queue");
            add("javax.jms.Topic");
            add("javax.jms.ConnectionFactory");

        }
    };

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
                if(produces != null) {
                    builder.removeFromField(field, Produces.class);
                }
            }

        }
        if(modified) {
            pat.setAnnotatedType(builder.create());
        }
    }

    private <T> boolean doQualifyByResourceQualifier(final AnnotatedField<? super T> field) {
        String typeName = field.getJavaMember().getType().getName();
        return !resourceToIgnore.contains(typeName);
    }

    private <T> void doResourceQualifyIfNecessary(final AnnotatedTypeBuilder<T> builder, final AnnotatedField<? super T> field, final Resource resource) {
        if(field.getAnnotation(Produces.class) == null && doQualifyByResourceQualifier(field)) {
            if(resource != null && !(resource.name().isEmpty() && resource.mappedName().isEmpty() && resource.lookup().isEmpty())) {
                builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(resource.name(), resource.lookup(), resource.mappedName()));
            }
        }
    }
}
