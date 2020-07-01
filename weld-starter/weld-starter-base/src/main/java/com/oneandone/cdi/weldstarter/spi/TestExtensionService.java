package com.oneandone.cdi.weldstarter.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.Service;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;

/**
 * @author aschoerk
 */
public interface TestExtensionService {
    default List<Extension> getExtensions() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<? extends Annotation>> extraClassAnnotations() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<? extends Annotation>> extraFieldAnnotations() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<?>> handleExtraClassAnnotation(final Annotation annotation, Class<?> c) { return Collections.EMPTY_LIST; }

    default void explicitlyExcluded(Class<?> c) {};

    default Collection<Class<? extends Annotation>> injectAnnotations() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<?>> interceptorDecoratorSequence() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<?>> producingAnnotations() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<? extends Service>> serviceClasses() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<? extends Extension>> extensionClasses() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<?>> testClasses() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<?>> testAvailableClasses() {
        return Collections.EMPTY_LIST;
    }

    default void preStartupAction(WeldSetupClass weldSetup, Class clazz, Method method) {}

    default void postStartupAction(CreationalContexts creationalContexts, WeldStarter weldStarter) {}

    default void initAnalyze() {}

    /**
     * Available classes can be evaluated to be forced to be started. The evaluation also can show that some of those classes might be strong
     * candidates to be started.
     *
     * @param c the class
     * @return true if candidate is voted to be started.
     */
    default boolean candidateToStart(Class<?> c) {
        return false;
    }

    default List<? extends Class<?>> fakeClasses() {
        return Collections.EMPTY_LIST;
    }

    default Collection<? extends Class<?>> excludeFromIndexScan()  {
        return Collections.EMPTY_LIST;
    }

    default Collection<? extends Class<?>> excludeAsInjects()  {
        return Collections.EMPTY_LIST;
    }

    default void addQualifiers(Field f, Collection<Annotation> qualifiers)   {

    }

}
