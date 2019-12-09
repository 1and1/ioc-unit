package com.oneandone.iocunit.resteasy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Extension;
import javax.ws.rs.Path;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.oneandone.iocunit.resteasy.auth.AuthInterceptor;
import com.oneandone.iocunit.resteasy.auth.TestAuth;
import com.oneandone.iocunit.util.Annotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * @author aschoerk
 */
public class RestEasyTestExtensionServices implements TestExtensionService {

    static ThreadLocal<Set<Class>> perAnnotationDefinedJaxRSClasses = new ThreadLocal<>();
    static ThreadLocal<Set<Class>> asCandidatesDefinedJaxRSClasses = new ThreadLocal<>();
    static ThreadLocal<Boolean> onlyAnnotationDefined = new ThreadLocal<>();

    public static ThreadLocal<TestAuth> testSecurityThreadLocal = new ThreadLocal<>();

    private static Logger logger = LoggerFactory.getLogger(RestEasyTestExtensionServices.class);

    @Override
    public void initAnalyze() {
        perAnnotationDefinedJaxRSClasses.set(new HashSet<>());
        asCandidatesDefinedJaxRSClasses.set(new HashSet<>());
        onlyAnnotationDefined.set(false);
    }

    @Override
    public List<Extension> getExtensions() {
        List<Extension> result = new ArrayList<>();
        try {
            if (Path.class.getName() != null)
                result.add(new JaxRsRestEasyTestExtension());
        } catch (NoClassDefFoundError ex) {
            ;
        }

        return result;
    }


    @Override
    public List<Class<?>> testClasses() {
        List<Class<?>> result = new ArrayList<>();
        result.add(RestEasyMockInit.class);
        result.add(AuthInterceptor.class);
        return result;
    }

    @Override
    public List<Class<? extends Annotation>> extraClassAnnotations() {
        return Arrays.asList(JaxRSClasses.class);
    }

    @Override
    public void handleExtraClassAnnotation(final Annotation annotation, Class<?> c) {
        if(annotation.annotationType().equals(JaxRSClasses.class)) {
            final JaxRSClasses jaxrsAnnotation = (JaxRSClasses) annotation;
            Class<?>[] jaxRSClassesForThis = jaxrsAnnotation.value();
            if (jaxrsAnnotation.onlyDefinedByAnnotation()) {
                onlyAnnotationDefined.set(true);
            }
            if(jaxRSClassesForThis != null) {
                for (Class<?> clazz : jaxRSClassesForThis) {
                    perAnnotationDefinedJaxRSClasses.get().add(clazz);
                }
            }

        }
    }
    @Override
    public void postStartupAction(final CreationalContexts creationalContexts, final WeldStarter weldStarter) {
        creationalContexts.create(RestEasyMockInit.class, ApplicationScoped.class);
    }

    /**
     * Available classes can be evaluated to be forced to be started. The evaluation also can show that some of those classes might be strong
     * candidates to be started.
     *
     * @param c the class
     * @return true if candidate is voted to be started.
     */
    @Override
    public boolean candidateToStart(final Class<?> c) {
        if (c.isInstance(SecurityContext.class)) {
            logger.trace("Found SecurityContext in class: {}",c.getName());
        }
        if (c.isAnnotationPresent(Provider.class) || JaxRsRestEasyTestExtension.annotationPresent(c, Path.class)) {
            asCandidatesDefinedJaxRSClasses.get().add(c);
        }
        if (perAnnotationDefinedJaxRSClasses.get().contains(c))
            return true;
        else
            return false;
    }

    @Override
    public void preStartupAction(WeldSetupClass weldSetup, Class clazz, Method method) {
        for (Class<?> c : perAnnotationDefinedJaxRSClasses.get()) {
            if (!weldSetup.getBeanClasses().contains(c.getName())) {
                logger.info("Restresource or ExceptionMapper candidate: {} found "
                            + " added to testconfiguration.", c.getSimpleName());
                weldSetup.getBeanClasses().add(c.getName());
            }
        }
        for (Class<?> c : asCandidatesDefinedJaxRSClasses.get()) {
            if (!weldSetup.getBeanClasses().contains(c.getName())) {
                logger.warn("Restresource or ExceptionMapper candidate: {} found "
                            + " while scanning availables, but not in testconfiguration included.", c.getSimpleName());
            }
        }
        asCandidatesDefinedJaxRSClasses.get().clear(); // show only once
        TestAuth testAuth = Annotations.findAnnotation(clazz, method, TestAuth.class);
        if (testAuth != null) {
            testSecurityThreadLocal.set(testAuth);
            weldSetup.getBeanClasses().add(IocUnitSecurityContext.class.getName());
        } else {
            testSecurityThreadLocal.set(null);
        }
    }
}
