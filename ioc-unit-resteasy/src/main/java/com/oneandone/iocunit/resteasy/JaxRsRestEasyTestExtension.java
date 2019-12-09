package com.oneandone.iocunit.resteasy;

import com.oneandone.iocunit.resteasy.auth.RestEasyAuthorized;
import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.*;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;

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

    public static <T> boolean annotationPresent(Class aClass, Class annotation) {
        if (aClass == null || aClass.equals(Object.class))
            return false;

        if (aClass.isAnnotationPresent(annotation)) {
            return true;
        }
        if (!annotationPresent(aClass.getSuperclass(), annotation)) {
            for (Class c : aClass.getInterfaces()) {
                if (annotationPresent(c, annotation))
                    return true;
            }
        }
        return false;
    }

    public <T> void processAnnotatedType(@Observes
                                         @WithAnnotations({Path.class,
                                                 GET.class, POST.class, PUT.class, DELETE.class,
                                                 Provider.class})
                                                 ProcessAnnotatedType<T> pat) {
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        final Class aClass = annotatedType.getJavaClass();
        if (annotationPresent(aClass, Path.class)) {
            resourceClasses.add(aClass);
        }
        if (annotatedType.isAnnotationPresent(Provider.class)) {
            providers.add(aClass);
        }
    }

    public <T> void processSecureType(@Observes
                                      @WithAnnotations({RunAs.class,
                                              RolesAllowed.class, PermitAll.class, DenyAll.class})
                                              ProcessAnnotatedType<T> pat) {
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        RestEasyAuthorized toAdd = AnnotationInstanceProvider.of(RestEasyAuthorized.class);
        AnnotatedTypeBuilder<T> builder =
                new AnnotatedTypeBuilder<T>().readFromType(annotatedType);
        builder.addToClass(toAdd);
        pat.setAnnotatedType(builder.create());
    }
}
