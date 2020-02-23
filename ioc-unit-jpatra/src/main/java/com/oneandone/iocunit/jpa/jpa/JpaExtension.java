/*
 * Copyright 2014 Bryn Cooke Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.oneandone.iocunit.jpa.jpa;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.jpa.tra.SimulatedTransactionManager;

/**
 * CDI-Extension used to handle @Resource, @PersistenceContext...
 * normally it just adds @Inject to the declarations.
 * This was originally checked in at cdi-unit and has been adapted.
 */
@ApplicationScoped
public class JpaExtension implements Extension {

    Logger logger = LoggerFactory.getLogger("CDI-Unit JPA-ExtensionExtended");

    private List<Class<?>> entityClasses = new ArrayList<>();

    private static AnnotationLiteral<Default> createDefaultAnnotation() {
        return new AnnotationLiteral<Default>() {
            private static final long serialVersionUID = 1L;
        };
    }

    private static AnnotationLiteral<Dependent> createDependentAnnotation() {
        return new AnnotationLiteral<Dependent>() {
            private static final long serialVersionUID = 1L;
        };
    }

    private static AnnotationLiteral<ApplicationScoped> createApplicationScopedAnnotation() {
        return new AnnotationLiteral<ApplicationScoped>() {
            private static final long serialVersionUID = 1L;
        };
    }

    public List<Class<?>> getEntityClasses() {
        return entityClasses;
    }

    /**
     * use this event to initialise static contents in SimulatedTransactionManager
     *
     * @param bbd not used
     * @param <T> not used
     */
    public <T> void processBeforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
        new SimulatedTransactionManager().init();
    }

    <T extends Annotation> boolean isAnnotationPresent(Class<?> annotatedType, Class<T> annotation) {
        if(annotatedType.equals(Object.class)) {
            return false;
        }
        return annotatedType.isAnnotationPresent(annotation);
    }

    /**
     * Handle Bean classes, if EJB-Annotations are recognized change, add, remove as fitting.
     *
     * @param pat the description of the beanclass
     * @param <T> The type
     */
    public <T> void processAnnotatedType(@Observes
                                         @WithAnnotations({
                                                 Entity.class,
                                                 MappedSuperclass.class,
                                                 Resource.class,
                                                 PersistenceContext.class,
                                                 PersistenceUnit.class
                                         }) ProcessAnnotatedType<T> pat) {
        logger.trace("processing annotated Type: " + pat.getAnnotatedType().getJavaClass().getName());

        boolean modified = false;
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType);

        boolean scopeIsPresent =
                annotatedType.isAnnotationPresent(ApplicationScoped.class)
                || annotatedType.isAnnotationPresent(Dependent.class)
                || annotatedType.isAnnotationPresent(RequestScoped.class)
                || annotatedType.isAnnotationPresent(SessionScoped.class);

        Entity entity = annotatedType.getAnnotation(Entity.class);
        if(entity != null) {
            entityClasses.add(annotatedType.getJavaClass());
        }
        MappedSuperclass mappedSuperclass = annotatedType.getAnnotation(MappedSuperclass.class);
        if(mappedSuperclass != null) {
            entityClasses.add(annotatedType.getJavaClass());
        }


        boolean makeApplicationScoped = false;
        for (AnnotatedField<? super T> field : annotatedType.getFields()) {
            boolean addInject = false;
            Resource resource = field.getAnnotation(Resource.class);
            PersistenceContext persistenceContext = field.getAnnotation(PersistenceContext.class);
            if(resource != null) {  // all Resources will be set injected. The Tester must provide anything for them.
                // this means that MessageDrivenContexts, SessionContext and JMS-Resources will be expected to be injected.
                addInject = true;
            }
            if(field.getAnnotation(PersistenceContext.class) != null) {
                addInject = true;
                builder.removeFromField(field, PersistenceContext.class);
            }
            if(addInject) {
                modified = true;
                builder.addToField(field, new AnnotationLiteral<Inject>() {
                    private static final long serialVersionUID = 1L;
                });
                Produces produces = field.getAnnotation(Produces.class);


                final String typeName = field.getBaseType().getTypeName();
                switch (typeName) {
                    case "java.lang.String":
                        builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(resource.name(), resource.lookup(), resource.mappedName()));
                        break;
                    case "java.sql.DataSource":
                        doResourceQualifyIfNecessary(builder, field, resource);
                        break;
                    case "javax.transaction.UserTransaction":
                        // no resource-qualifier necessary, type specifies enough
                        break;
                    case "javax.persistence.EntityManager":
                        if(produces == null && persistenceContext != null &&
                           (persistenceContext.name() != null && !persistenceContext.name().isEmpty()
                            || persistenceContext.unitName() != null && !persistenceContext.unitName().isEmpty())) {
                            builder.addToField(field, new PersistenceContextQualifier.PersistenceContextQualifierLiteral(persistenceContext.name(), persistenceContext.unitName()));
                        }
                        break;
                    default:
                        doResourceQualifyIfNecessary(builder, field, resource);
                        break;
                }
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

    private <T> void doResourceQualifyIfNecessary(final AnnotatedTypeBuilder<T> builder, final AnnotatedField<? super T> field, final Resource resource) {
        if(field.getAnnotation(Produces.class) == null) {
            if(resource != null && !(resource.name().isEmpty() && resource.mappedName().isEmpty() && resource.lookup().isEmpty())) {
                builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(resource.name(), resource.lookup(), resource.mappedName()));
            }
        }
    }

    public <X> void processTransactionalMember(
            @Observes
                    AnnotatedMember annotatedMethod) {
        logger.info("Member: " + annotatedMethod.toString());
    }

    void processManagedBean(@Observes ProcessManagedBean<?> event) {
        // LOGGER.fine("Handling ProcessManagedBean event for " + event.getBean().getBeanClass().getName());

        // TODO - here we should check that all the rules have been followed
        // and call addDefinitionError for each problem we encountered

        Bean<?> bean = event.getBean();
        for (InjectionPoint injectionPoint : bean.getInjectionPoints()) {
            StringBuilder sb = new StringBuilder();
            sb.append("  Found injection point ");
            sb.append(injectionPoint.getType());
            if(injectionPoint.getMember() != null && injectionPoint.getMember().getName() != null) {

                sb.append(": ");
                sb.append(injectionPoint.getMember().getName());
            }
            for (Annotation annotation : injectionPoint.getQualifiers()) {
                sb.append(" ");
                sb.append(annotation);
            }
            logger.trace(sb.toString());
        }
    }
}
