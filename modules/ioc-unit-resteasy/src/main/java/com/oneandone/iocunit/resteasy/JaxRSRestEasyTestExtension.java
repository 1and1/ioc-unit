package com.oneandone.iocunit.resteasy;

import static com.oneandone.iocunit.resteasy.RestEasyTestExtensionServices.perAnnotationDefinedJaxRSClasses;

import java.lang.reflect.Method;
import java.util.HashSet;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.ExtensionSupport;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.iocunit.resteasy.auth.AuthInterceptor;
import com.oneandone.iocunit.resteasy.auth.RestEasyAuthorized;
import com.oneandone.iocunit.resteasy.servlet.IocUnitHttpServletRequest;
import com.oneandone.iocunit.resteasy.servlet.IocUnitHttpSession;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class JaxRSRestEasyTestExtension implements Extension {
    Logger logger = LoggerFactory.getLogger("IOCUnit JaxRSRestEasyTestExtension");
    HashSet<Class> resourceClasses = new HashSet<>();
    HashSet<Class> providers = new HashSet<>();

    public <T> void processAfterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {

        ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, RestEasyMockInit.class);
        ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, AuthInterceptor.class);
        ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, IocUnitResteasyHttpClient.class);
        try {
            Class<?> tmp = Class.forName("javax.servlet.http.HttpSession.class");
            ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, IocUnitHttpSession.class);
            ExtensionSupport.addTypeAfterBeanDiscovery(abd, bm, IocUnitHttpServletRequest.class);
        } catch (Exception e) {
            logger.info("Resteasy usage without HttpSession-Class.");
        }
    }

    private void addType(final BeforeBeanDiscovery bbd, final BeanManager bm, final Class<?> c) {
        AnnotatedType<? extends Object> at = bm.createAnnotatedType(c);
        bbd.addAnnotatedType(at, "EjbExtensionExtended_" + c.getName());
    }

    public HashSet<Class> getResourceClasses() {
        return resourceClasses;
    }

    public HashSet<Class> getProviders() {
        return providers;
    }

    public static <T> boolean annotationPresent(Class aClass, Class annotation) {
        if(aClass == null || aClass.equals(Object.class)) {
            return false;
        }

        if(aClass.isAnnotationPresent(annotation)) {
            return true;
        }
        if(!annotationPresent(aClass.getSuperclass(), annotation)) {
            for (Class c : aClass.getInterfaces()) {
                if(annotationPresent(c, annotation)) {
                    return true;
                }
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
        if(annotationPresent(aClass, Path.class)) {
            resourceClasses.add(aClass);
        }
        if(annotatedType.isAnnotationPresent(Provider.class)) {
            providers.add(aClass);
        }
    }

    private boolean hasAuthAnnotation(Method m) {
        return m.isAnnotationPresent(RunAs.class) ||
               m.isAnnotationPresent(RolesAllowed.class) ||
               m.isAnnotationPresent(PermitAll.class) ||
               m.isAnnotationPresent(DenyAll.class);
    }

    private boolean hasAuthAnnotation(Class c) {
        return c.isAnnotationPresent(RunAs.class) ||
               c.isAnnotationPresent(RolesAllowed.class) ||
               c.isAnnotationPresent(PermitAll.class) ||
               c.isAnnotationPresent(DenyAll.class);
    }

    public <T> void processSecureType(@Observes
                                      @WithAnnotations({RunAs.class,
                                              RolesAllowed.class, PermitAll.class, DenyAll.class})
                                              ProcessAnnotatedType<T> pat) {
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();

        try {
            if(WeldSetupClass.isWeld1()) {
                if(annotatedType.isAnnotationPresent(Provider.class)) {
                    return;
                }
                final Class<T> javaClass = annotatedType.getJavaClass();
                if(!needsAuthInterceptorClassDeep(javaClass)) {
                    return;
                }
            }
        } catch (RuntimeException | NoClassDefFoundError e) {
            ;
        }
        makeInterceptedForAuth(pat, annotatedType);
    }

    private <T> void makeInterceptedForAuth(final ProcessAnnotatedType<T> pat, final AnnotatedType<T> annotatedType) {
        RestEasyAuthorized toAdd = AnnotationInstanceProvider.of(RestEasyAuthorized.class);
        AnnotatedTypeBuilder<T> builder =
                new AnnotatedTypeBuilder<T>().readFromType(annotatedType);
        builder.addToClass(toAdd);
        pat.setAnnotatedType(builder.create());
    }

    public <T> void processSecureJaxRSTypes(@Observes ProcessAnnotatedType<T> pat) {
        AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        final Class aClass = annotatedType.getJavaClass();
        if(perAnnotationDefinedJaxRSClasses != null &&
           perAnnotationDefinedJaxRSClasses.get() != null &&
           perAnnotationDefinedJaxRSClasses.get().contains(aClass)) {
            if(!aClass.isAnnotationPresent(Provider.class) && needsAuthInterceptorClassDeep(aClass)) {
                makeInterceptedForAuth(pat, annotatedType);
            }
        }
    }

    private <T> boolean needsAuthInterceptorClassDeep(final Class<T> javaClass) {
        if (javaClass == null)
            return false;
        if(needsAuthInterceptorClass(javaClass)) {
            return true;
        }
        if(needsAuthInterceptorClassDeep(javaClass.getSuperclass())) {
            return true;
        }
        for (Class i : javaClass.getInterfaces()) {
            if(needsAuthInterceptorClass(i)) {
                return true;
            }
        }
        return false;
    }


    private <T> boolean needsAuthInterceptorClass(final Class<T> javaClass) {
        if (javaClass == null || javaClass.equals(Object.class))
            return false;
        if(!hasAuthAnnotation(javaClass)) {
            for (Method m : javaClass.getDeclaredMethods()) {
                if(hasAuthAnnotation(m)) {
                    return true;
                }
            }
            return false;
        }
        else {
            return true;
        }
    }
}
