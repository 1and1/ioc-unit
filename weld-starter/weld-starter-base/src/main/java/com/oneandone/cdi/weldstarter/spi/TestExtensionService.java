package com.oneandone.cdi.weldstarter.spi;

import java.lang.annotation.Annotation;
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
    default Collection<Extension> getExtensions() {
        return Collections.EMPTY_LIST;
    }

    default Collection<Class<? extends Annotation>> extraClassAnnotations() {
        return Collections.EMPTY_LIST;
    }

    default Collection<Class<? extends Annotation>> extraFieldAnnotations() {
        return Collections.EMPTY_LIST;
    }

    default void handleExtraClassAnnotation(final Annotation annotation, Class<?> c) {}

    default Collection<Class<? extends Annotation>> injectAnnotations() {
        return Collections.EMPTY_LIST;
    }

    default List<Class<?>> interceptorDecoratorSequence() {
        return Collections.EMPTY_LIST;
    }

    default Collection<Class<?>> producingAnnotations() {
        return Collections.EMPTY_LIST;
    }

    default Collection<Class<? extends Service>> serviceClasses() {
        return Collections.EMPTY_LIST;
    }

    default Collection<Class<? extends Extension>> extensionClasses() {
        return Collections.EMPTY_LIST;
    }

    default Collection<Class<?>> testClasses() {
        return Collections.EMPTY_LIST;
    }

    default void preStartupAction(WeldSetupClass weldSetup) {}

    default void postStartupAction(CreationalContexts creationalContexts) {}

    default void initAnalyze() {}

    default boolean isSutClass(Class<?> c) { return false; }
}
