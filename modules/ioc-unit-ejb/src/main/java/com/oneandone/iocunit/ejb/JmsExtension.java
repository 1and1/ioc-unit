package com.oneandone.iocunit.ejb;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class JmsExtension extends EjbExtensionBase implements Extension {
    Logger logger = LoggerFactory.getLogger("CDI-Unit JMS-Extension");

    /**
     * Handle Bean classes, if EJB-Annotations are recognized change, add, remove as fitting.
     *
     * @param pat the description of the beanclass
     * @param <T> The type
     */
    public <T> void processAnnotatedType(@Observes
                                         @WithAnnotations({
                                                 JMSConnectionFactory.class
                                         })
                                                 ProcessAnnotatedType<T> pat) {
        logger.trace("processing annotated Type: " + pat.getAnnotatedType().getJavaClass().getName());

        boolean modified = false;
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType);

        for (AnnotatedField<? super T> field : annotatedType.getFields()) {
            boolean addInject = false;
            if(field.getAnnotation(JMSConnectionFactory.class) != null) {
                addInject = true;
                builder.removeFromField(field, JMSConnectionFactory.class);
            }
            if(addInject) {
                modified = true;
                builder.addToField(field, new AnnotationLiteral<Inject>() {
                    private static final long serialVersionUID = 1L;
                });
                Produces produces = field.getAnnotation(Produces.class);

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


}

