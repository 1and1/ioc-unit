package com.oneandone.iocunit.resteasy;

import java.util.HashSet;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class JaxRsRestEasyTestExtension implements Extension {

    HashSet<Class> resourceClasses = new HashSet<>();
    HashSet<Class> providers = new HashSet<>();


    public HashSet<Class> getResourceClasses() {
        return resourceClasses;
    }

    public HashSet<Class> getProviders() {
        return providers;
    }

    private <T> boolean annotationPresent(Class aClass, Class annotation) {
        if (aClass == null || aClass.equals(Object.class))
            return false;

        if(aClass.isAnnotationPresent(annotation)) {
            return true;
        }
        if (!annotationPresent(aClass.getSuperclass(), annotation)) {
            for (Class c: aClass.getInterfaces()) {
                if (annotationPresent(c, annotation))
                    return true;
            }
        }
        return false;
    }

    public <T> void processAnnotatedType(@Observes
                                         @WithAnnotations({
                                                 Path.class,
                                                 Provider.class
                                         }) ProcessAnnotatedType<T> pat) {
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        final Class aClass = annotatedType.getJavaClass();
        if (annotationPresent(aClass, Path.class)) {
            resourceClasses.add(aClass);
        }
        if(annotatedType.isAnnotationPresent(Provider.class)) {
            providers.add(aClass);
        }
    }
}
