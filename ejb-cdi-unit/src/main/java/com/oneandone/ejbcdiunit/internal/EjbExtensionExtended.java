/*
 * Copyright 2014 Bryn Cooke Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.oneandone.ejbcdiunit.internal;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.PersistenceContext;

import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.jboss.weld.bean.proxy.InterceptionDecorationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.ResourceQualifier;
import com.oneandone.ejbcdiunit.SupportEjbExtended;
import com.oneandone.ejbcdiunit.cdiunit.EjbName;
import com.oneandone.ejbcdiunit.persistence.SimulatedTransactionManager;
import com.oneandone.ejbcdiunit.resourcesimulators.SessionContextSimulation;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

/**
 * CDI-Extension used to handle @Resource, @PersistenceContext...
 * normally it just adds @Inject to the declarations.
 * This was originally checked in at cdi-unit and has been adapted.
 */
@SupportEjbExtended
@ApplicationScoped
public class EjbExtensionExtended implements Extension {

    Logger logger = LoggerFactory.getLogger("CDI-Unit EJB-ExtensionExtended");

    private List<Class<?>> timerClasses = new ArrayList<>();

    private List<Class<?>> entityClasses = new ArrayList<>();
    private List<Class<?>> startupSingletons = new ArrayList<>();

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

    public List<Class<?>> getTimerClasses() {
        return timerClasses;
    }

    public List<Class<?>> getStartupSingletons() {
        return startupSingletons;
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

    private <T> void processClass(AnnotatedTypeBuilder<T> builder, String name, boolean isSingleton, boolean scopeIsPresent) {
        logger.trace("processing class: {} singleton: {} scopeIsPresent: {}", name, isSingleton, scopeIsPresent);
        if (!scopeIsPresent) {
            if (!isSingleton || builder.getJavaClass().getFields().length > 0) {
                builder.addToClass(createDependentAnnotation());
            } else {
                builder.addToClass(createApplicationScopedAnnotation());  // For Singleton normally only ApplicationScoped
            }
        }

        builder.addToClass(createDefaultAnnotation());
        if (!name.isEmpty()) {
            builder.addToClass(new EjbName.EjbNameLiteral(name));
        } else {
            builder.addToClass(DefaultLiteral.INSTANCE);
        }
    }

    String beanNameOrName(EJB ejb) {
        if (!ejb.name().isEmpty()) {
            return ejb.name();
        } else {
            return ejb.beanName();
        }
    }

    <T extends Annotation> T findAnnotation(Class<?> annotatedType,  Class<T> annotation) {
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

    <T extends Annotation, X> boolean isAnnotationPresent(ProcessAnnotatedType<X> pat,  Class<T> annotation) {
        return isAnnotationPresent(pat.getAnnotatedType().getJavaClass(), annotation);
    }

    /**
     * Handle Bean classes, if EJB-Annotations are recognized change, add, remove as fitting.
     *
     * @param pat the description of the beanclass
     * @param <T> The type
     */
    public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
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
        if (entity != null) {
            entityClasses.add(annotatedType.getJavaClass());
        }

        Stateless stateless = findAnnotation(annotatedType.getJavaClass(), Stateless.class);

        // Stateless stateless = annotatedType.getJavaClass().getAnnotation(Stateless.class);

        if (stateless != null) {
            processClass(builder, stateless.name(), false, scopeIsPresent);
            modified = true;
        }

        Stateful stateful = findAnnotation(annotatedType.getJavaClass(),Stateful.class);

        if (stateful != null) {
            processClass(builder, stateful.name(), false, scopeIsPresent);
            modified = true;
        }
        try {
            Singleton singleton = findAnnotation(annotatedType.getJavaClass(),Singleton.class);
            if (singleton != null) {
                processClass(builder, singleton.name(), true, scopeIsPresent);
                modified = true;
                if (annotatedType.getAnnotation(Startup.class) != null) {
                    startupSingletons.add(annotatedType.getJavaClass());
                }
            }
        } catch (NoClassDefFoundError e) {
            // EJB 3.0
        }

        for (AnnotatedMethod<? super T> method : annotatedType.getMethods()) {
            EJB ejb = method.getAnnotation(EJB.class);
            if (ejb != null) {
                builder.removeFromMethod(method, EJB.class);
                modified = true;
                if (!beanNameOrName(ejb).isEmpty()) {
                    builder.addToMethod(method, new EjbName.EjbNameLiteral(beanNameOrName(ejb)));
                } else {
                    builder.addToMethod(method, DefaultLiteral.INSTANCE);
                }
            }
        }

        for (AnnotatedField<? super T> field : annotatedType.getFields()) {
            boolean addInject = false;
            Produces produces = field.getAnnotation(Produces.class);
            EJB ejb = field.getAnnotation(EJB.class);
            if (ejb != null) {
                modified = true;
                addInject = true;
                if (field.getJavaMember().getType().equals(annotatedType.getJavaClass())) {
                    logger.error("Self injection of EJB Type {} in field {} of Class {} can't get simulated by ejb-cdi-unit",
                            field.getJavaMember().getType().getName(), field.getJavaMember().getName(),
                            field.getJavaMember().getDeclaringClass().getName());
                }

                builder.removeFromField(field, EJB.class);
                if (!beanNameOrName(ejb).isEmpty()) {
                    builder.addToField(field, new EjbName.EjbNameLiteral(beanNameOrName(ejb)));
                } else {
                    builder.addToField(field, DefaultLiteral.INSTANCE);
                }
            }
            Resource resource = field.getAnnotation(Resource.class);
            if (resource != null) {  // all Resources will be set injected. The Tester must provide anything for them.
                // this means that MessageDrivenContexts, SessionContext and JMS-Resources will be expected to be injected.
                addInject = true;
            }
            if (field.getAnnotation(PersistenceContext.class) != null) {
                addInject = true;
                builder.removeFromField(field, PersistenceContext.class);
            }
            if (addInject) {
                modified = true;
                builder.addToField(field, new AnnotationLiteral<Inject>() {
                    private static final long serialVersionUID = 1L;
                });
                if (produces != null) {
                    builder.removeFromField(field, Produces.class);
                }
                if (field.getBaseType().equals(String.class)) {

                    builder.addToField(field, new ResourceQualifier.ResourceQualifierLiteral(resource.name(), resource.lookup(), resource.mappedName()) {
                        private static final long serialVersionUID = 1L;

                    });

                }
            }
        }
        if (modified) {
            pat.setAnnotatedType(builder.create());
        }
    }

    /**
     * create EJB-Wrapper, Interceptors to specific annotated types if necessary.
     *
     * @param pat the description of the type
     * @param <X> the type
     */
    public <X> void processInjectionTarget(
            @Observes ProcessAnnotatedType<X> pat) {
        if (isAnnotationPresent(pat, Stateless.class) || isAnnotationPresent(pat, Stateful.class)
                || isAnnotationPresent(pat, Singleton.class)
                || isAnnotationPresent(pat, MessageDriven.class)) {
            createEJBWrapper(pat, pat.getAnnotatedType());
        } else {
            if (possiblyAsynchronous(pat.getAnnotatedType())) {
                logger.error("Non Ejb with Asynchronous-Annotation {}", pat);
            }

        }
    }

    public <T> void initializeSelfInit(@Observes ProcessInjectionTarget<T> pit) {

        boolean needToWrap = false;
        for (AnnotatedField<? super T> f : pit.getAnnotatedType().getFields()) {
            if (f.getJavaMember().getType().equals(pit.getAnnotatedType().getJavaClass())) {
                needToWrap = true;
                break;
            }
        }

        if (needToWrap) {
            final InjectionTarget<T> it = pit.getInjectionTarget();
            final Set<AnnotatedField<? super T>> annotatedTypeFields = pit.getAnnotatedType().getFields();
            final Class<?> annotatedTypeJavaClass = pit.getAnnotatedType().getJavaClass();
            InjectionTarget<T> wrapped = new InjectionTarget<T>() {

                @Override
                public void inject(final T instance, CreationalContext<T> ctx) {
                    HashMap<AnnotatedField<? super T>, Object> orgValues = fetchOriginalValuesOfSelfFields(instance);
                    it.inject(instance, ctx);
                    // After injection replace all fields of self-type by enhanced ones which make sure interception is handled.
                    wrapDifferingValuesOfSelfFields(instance, orgValues);
                }


                @Override
                public void postConstruct(T instance) {
                    it.postConstruct(instance);
                }

                @Override
                public void preDestroy(T instance) {
                    it.dispose(instance);
                }

                @Override
                public void dispose(T instance) {
                    it.dispose(instance);
                }

                @Override
                public Set<InjectionPoint> getInjectionPoints() {
                    return it.getInjectionPoints();
                }

                @Override
                public T produce(CreationalContext<T> ctx) {
                    return it.produce(ctx);
                }

                private void wrapDifferingValuesOfSelfFields(T instance, HashMap<AnnotatedField<? super T>, Object> orgValues) {
                    for (AnnotatedField<? super T> f : annotatedTypeFields) {
                        if (f.getJavaMember().getType().equals(annotatedTypeJavaClass)) {
                            try {
                                final Field javaMember = f.getJavaMember();
                                javaMember.setAccessible(true);
                                final Object currentInstance = javaMember.get(instance);
                                if (currentInstance != null && currentInstance != orgValues.get(f)) {
                                    Enhancer enhancer = new Enhancer();
                                    enhancer.setSuperclass(currentInstance.getClass());
                                    enhancer.setCallback(new InvocationHandler() {
                                        @Override
                                        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                                            SessionContextSimulation.startInterceptionDecorationContext();
                                            try {
                                                return method.invoke(currentInstance, objects);
                                            } catch (Throwable thw) {
                                                if (thw instanceof InvocationTargetException) {
                                                    throw thw.getCause();
                                                } else {
                                                    throw thw;
                                                }
                                            } finally {
                                                InterceptionDecorationContext.endInterceptorContext();
                                            }
                                        }
                                    });
                                    javaMember.setAccessible(true);
                                    javaMember.set(instance, enhancer.create());
                                }
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                private HashMap<AnnotatedField<? super T>, Object> fetchOriginalValuesOfSelfFields(T instance) {
                    HashMap<AnnotatedField<? super T>, Object> orgValues = new HashMap<>();
                    for (AnnotatedField<? super T> f : annotatedTypeFields) {
                        if (f.getJavaMember().getType().equals(annotatedTypeJavaClass)) {
                            final Field javaMember = f.getJavaMember();
                            javaMember.setAccessible(true);
                            try {
                                orgValues.put(f, javaMember.get(instance));
                            } catch (IllegalAccessException e) {
                                new RuntimeException(e);
                            }
                        }
                    }
                    return orgValues;
                }

            };
            pit.setInjectionTarget(wrapped);
        }
    }


    private <X> boolean possiblyAsynchronous(final AnnotatedType<X> at) {

        boolean isTimer = false;
        boolean isAsynch = false;
        if (at.isAnnotationPresent(Asynchronous.class)) {
            return true;
        }

        for (AnnotatedMethod<? super X> m: at.getMethods()) {
            if (!isTimer && (m.isAnnotationPresent(Timeout.class)
                    || m.isAnnotationPresent(Schedule.class)
                    || m.isAnnotationPresent(Schedules.class)
                    )) {
                timerClasses.add(m.getJavaMember().getDeclaringClass());
                isTimer = true;
            }
            if (!isAsynch && m.isAnnotationPresent(Asynchronous.class)) {
                isAsynch = true;
            }
        }

        return isAsynch;
    }

    private <X> void createEJBWrapper(ProcessAnnotatedType<X> pat,
            final AnnotatedType<X> at) {
        EjbAsynchronous ejbAsynchronous = AnnotationInstanceProvider.of(EjbAsynchronous.class);

        EjbTransactional transactionalAnnotation =
                AnnotationInstanceProvider.of(EjbTransactional.class);

        AnnotatedTypeBuilder<X> builder =
                new AnnotatedTypeBuilder<X>().readFromType(at);
        builder.addToClass(transactionalAnnotation);
        if (possiblyAsynchronous(at)) {
            builder.addToClass(ejbAsynchronous);
        }

        // by annotating let CDI set Wrapper to this Bean
        pat.setAnnotatedType(builder.create());
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
            if (injectionPoint.getMember() != null && injectionPoint.getMember().getName() != null) {

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
