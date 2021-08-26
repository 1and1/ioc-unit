package com.oneandone.iocunit.jtajpa;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import com.oneandone.cdi.weldstarter.ExtensionSupport;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerWrapper;

/**
 * @author aschoerk
 */
public class JPAAnnotationsExtension implements Extension {

    public <T> void processBeforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager bm) {
        bbd.addQualifier(PersistenceContextQualifier.class);
    }


    public <T> void processAfterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, EntityManagerFactoryFactory.class);
        ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, EntityManagerWrapper.class);
    }


    public <T> void processAnnotatedType(@Observes @WithAnnotations(PersistenceContext.class)
                                                 ProcessAnnotatedType<T> pat) {
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType);

        final Class aClass = annotatedType.getJavaClass();
        boolean modified = false;
        for (AnnotatedField<? super T> field : annotatedType.getFields()) {
            PersistenceContext persistenceContext = field.getAnnotation(PersistenceContext.class);
            if(persistenceContext != null) {  // all PersistenceContextes will be set injected. The Tester must provide anything for them.
                modified = true;
                builder.addToField(field, new AnnotationLiteral<Inject>() {
                    private static final long serialVersionUID = 1L;
                });
                Produces produces = field.getAnnotation(Produces.class);
                doPersistenceContextQualifyIfNecessary(builder, field, persistenceContext);
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
    private <T> void doPersistenceContextQualifyIfNecessary(final AnnotatedTypeBuilder<T> builder, final AnnotatedField<? super T> field, final PersistenceContext persistenceContext) {
        if (field.getAnnotation(Produces.class) == null) {
            if(persistenceContext != null && !(persistenceContext.name().isEmpty() && persistenceContext.unitName().isEmpty())) {
                builder.addToField(field, new PersistenceContextQualifier.PersistenceContextQualifierLiteral(persistenceContext.name(), persistenceContext.unitName()));
            }
        }
    }
}
