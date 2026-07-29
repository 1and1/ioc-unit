package com.oneandone.iocunit.analyzer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanAttributes;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProducerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compensates for a gap in ioc-unit's bean-discovery process: {@link Phase1Analyzer} never registers
 * abstract classes as Weld bean classes (Weld itself would reject them anyway, see the CDI spec), so any
 * {@code @Produces} field or method declared only on an abstract superclass of a registered, concrete
 * bean class is invisible to Weld's own automatic producer discovery.
 * <p>
 * Up to (including) Weld {@code 5.1.0.Final} this went unnoticed: a bug in Weld's own
 * {@code EnhancedAnnotatedTypeImpl} (WELD-001408) attributed every inherited field - including producer
 * fields declared on an abstract superclass - to the concrete subclass being scanned, so Weld created a
 * producer bean for it anyway, purely by accident. Weld {@code 5.1.6.Final} fixed that attribution to use
 * plain reflection ({@code Field.getDeclaringClass()}), which is correct in general, but removed the
 * accidental compensation for the gap described above - so producer fields/methods on abstract test
 * superclasses stopped being picked up.
 * <p>
 * This extension restores that behaviour, but does so in a way that stays fully within the CDI
 * specification: instead of trying to make Weld treat the abstract class as a bean (impossible - abstract
 * classes can never become managed beans), it uses the official {@link AfterBeanDiscovery#addBean(Bean)}
 * SPI to synthesize exactly the producer bean the CDI inheritance model says should exist, logically
 * owned by the concrete subclass, for every {@code @Produces} member it finds declared on an abstract
 * ancestor of a registered bean class.
 *
 */
public class AbstractSuperclassProducerExtension implements Extension {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSuperclassProducerExtension.class);

    private final Collection<Class<?>> beanClasses;

    @SuppressWarnings("unused")
    public AbstractSuperclassProducerExtension() {
        // never used at runtime: this extension is always instantiated explicitly by SetupCreator
        // with the actual bean-class list; the no-arg constructor only exists so that CDI tooling
        // does not flag this class (which happens to implement the Extension marker interface) as
        // an invalid managed bean.
        this(Collections.emptyList());
    }

    public AbstractSuperclassProducerExtension(final Collection<Class<?>> beanClasses) {
        this.beanClasses = beanClasses;
    }

    @SuppressWarnings("unused")
    void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd, final BeanManager bm) {
        for (Class<?> beanClass : beanClasses) {
            for (Class<?> ancestor = beanClass.getSuperclass();
                 ancestor != null && ancestor != Object.class;
                 ancestor = ancestor.getSuperclass()) {
                if(!Modifier.isAbstract(ancestor.getModifiers())) {
                    // Weld's own discovery already handles producers on concrete, registered classes.
                    continue;
                }
                handleAbstractAncestor(abd, bm, beanClass, ancestor);
            }
        }
    }

    private void handleAbstractAncestor(final AfterBeanDiscovery abd, final BeanManager bm,
            final Class<?> beanClass, final Class<?> abstractAncestor) {
        for (Field field : abstractAncestor.getDeclaredFields()) {
            if(isProducer(field.getAnnotations()) && !isFieldShadowed(beanClass, abstractAncestor, field)) {
                addProducerFieldBean(abd, bm, beanClass, field);
            }
        }
        for (Method method : abstractAncestor.getDeclaredMethods()) {
            if(isProducer(method.getAnnotations()) && !isMethodOverridden(beanClass, abstractAncestor, method)) {
                addProducerMethodBean(abd, bm, beanClass, method);
            }
        }
    }

    /**
     * CDI only treats an inherited producer method as belonging to the concrete bean "unless
     * overridden" (spec wording). This checks whether any class between {@code abstractAncestor}
     * (exclusive) and {@code beanClass} (inclusive) redeclares a method with the same signature -
     * regardless of whether that override itself still carries {@code @Produces}. If it does, the
     * override - not the ancestor's original declaration - is what determines the effective producer
     * (Weld's normal discovery already handles that case correctly), so the ancestor's version must
     * not be synthesized here too, or the two would collide as ambiguous producers for the same type.
     */
    private boolean isMethodOverridden(final Class<?> beanClass, final Class<?> abstractAncestor, final Method method) {
        if(Modifier.isPrivate(method.getModifiers())) {
            return false;
        }
        for (Class<?> c = beanClass; c != null && c != abstractAncestor; c = c.getSuperclass()) {
            for (Method candidate : c.getDeclaredMethods()) {
                if(candidate.getName().equals(method.getName())
                   && Arrays.equals(candidate.getParameterTypes(), method.getParameterTypes())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Java fields are hidden, not overridden, but for producer purposes a field re-declared (shadowed)
     * by a more derived class must be treated the same way as an overridden method: only the more
     * derived declaration is relevant, so the ancestor's field must not also be synthesized here.
     * <p>
     * A {@code private} field can never actually be hidden by a subclass - it isn't part of the
     * inherited member namespace at all, so a same-named field somewhere below it in the hierarchy is
     * just an unrelated field, not a shadow (this is exactly the case for the typical
     * {@code @Produces private Bean bean;} pattern together with an unrelated {@code @Inject Bean bean;}
     * injection point in the concrete test class).
     */
    private boolean isFieldShadowed(final Class<?> beanClass, final Class<?> abstractAncestor, final Field field) {
        if(Modifier.isPrivate(field.getModifiers())) {
            return false;
        }
        for (Class<?> c = beanClass; c != null && c != abstractAncestor; c = c.getSuperclass()) {
            for (Field candidate : c.getDeclaredFields()) {
                if(candidate.getName().equals(field.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isProducer(final Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().equals(Produces.class)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addProducerFieldBean(final AfterBeanDiscovery abd, final BeanManager bm,
            final Class<?> beanClass, final Field field) {
        try {
            final AnnotatedType<?> concreteType = bm.createAnnotatedType(beanClass);
            final AnnotatedField<?> annotatedField = findAnnotatedField(concreteType, field);
            if(annotatedField == null) {
                logger.warn("Could not re-locate producer field {} of {} on {}, skipping synthesized bean",
                        field, field.getDeclaringClass(), beanClass);
                return;
            }
            final BeanAttributes attributes = bm.createBeanAttributes(annotatedField);
            final Bean<?> declaringBean = resolveBean(bm, beanClass);
            final ProducerFactory producerFactory = bm.getProducerFactory((AnnotatedField) annotatedField, declaringBean);
            final Bean<?> synthesizedBean = bm.createBean(attributes, beanClass, producerFactory);
            logger.info("Synthesizing producer bean for field {} inherited by {} from abstract superclass {}",
                    field.getName(), beanClass.getName(), field.getDeclaringClass().getName());
            abd.addBean(synthesizedBean);
        } catch (RuntimeException e) {
            logger.warn("Could not synthesize producer bean for field {} of {} inherited by {}: {}",
                    field.getName(), field.getDeclaringClass().getName(), beanClass.getName(), e.toString());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addProducerMethodBean(final AfterBeanDiscovery abd, final BeanManager bm,
            final Class<?> beanClass, final Method method) {
        try {
            final AnnotatedType<?> concreteType = bm.createAnnotatedType(beanClass);
            final AnnotatedMethod<?> annotatedMethod = findAnnotatedMethod(concreteType, method);
            if(annotatedMethod == null) {
                logger.warn("Could not re-locate producer method {} of {} on {}, skipping synthesized bean",
                        method, method.getDeclaringClass(), beanClass);
                return;
            }
            final BeanAttributes attributes = bm.createBeanAttributes(annotatedMethod);
            final Bean<?> declaringBean = resolveBean(bm, beanClass);
            final ProducerFactory producerFactory = bm.getProducerFactory((AnnotatedMethod) annotatedMethod, declaringBean);
            final Bean<?> synthesizedBean = bm.createBean(attributes, beanClass, producerFactory);
            logger.info("Synthesizing producer bean for method {} inherited by {} from abstract superclass {}",
                    method.getName(), beanClass.getName(), method.getDeclaringClass().getName());
            abd.addBean(synthesizedBean);
        } catch (RuntimeException e) {
            logger.warn("Could not synthesize producer bean for method {} of {} inherited by {}: {}",
                    method.getName(), method.getDeclaringClass().getName(), beanClass.getName(), e.toString());
        }
    }

    private AnnotatedField<?> findAnnotatedField(final AnnotatedType<?> concreteType, final Field field) {
        for (AnnotatedField<?> annotatedField : concreteType.getFields()) {
            if(annotatedField.getJavaMember().getName().equals(field.getName())
               && annotatedField.getJavaMember().getDeclaringClass().equals(field.getDeclaringClass())) {
                return annotatedField;
            }
        }
        return null;
    }

    private AnnotatedMethod<?> findAnnotatedMethod(final AnnotatedType<?> concreteType, final Method method) {
        for (AnnotatedMethod<?> annotatedMethod : concreteType.getMethods()) {
            if(annotatedMethod.getJavaMember().getName().equals(method.getName())
               && annotatedMethod.getJavaMember().getDeclaringClass().equals(method.getDeclaringClass())
               && Arrays.equals(annotatedMethod.getJavaMember().getParameterTypes(), method.getParameterTypes())) {
                return annotatedMethod;
            }
        }
        return null;
    }

    /**
     * Resolves the already-discovered {@link Bean} representing {@code beanClass} itself, so the
     * synthesized producer can be evaluated against the concrete bean instance that actually inherits
     * the producer field/method - exactly the instance CDI's inheritance model considers to "own" it.
     */
    private Bean<?> resolveBean(final BeanManager bm, final Class<?> beanClass) {
        final Set<Bean<?>> candidates = new HashSet<>(bm.getBeans(beanClass));
        if(candidates.isEmpty()) {
            throw new IllegalStateException("No bean found for " + beanClass
                                             + ", cannot resolve declaring bean for synthesized producer");
        }
        return bm.resolve(candidates);
    }
}
