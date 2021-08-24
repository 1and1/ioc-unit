package com.oneandone.iocunit.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * @author aschoerk
 */
public class TestExtensionServices implements TestExtensionService {

    static ThreadLocal<Set<Class>> testExtensionServiceData = new ThreadLocal<>();

    private static Logger logger = LoggerFactory.getLogger(TestExtensionServices.class);

    @Override
    public void initAnalyze() {
        if(testExtensionServiceData.get() == null) {
            testExtensionServiceData.set(new HashSet<>());
        }
    }

    @Override
    public void postStartupAction(final CreationalContexts creationalContexts, final WeldStarter weldStarter) {

    }

    @Override
    public List<Extension> getExtensions() {
        List<Extension> result = new ArrayList<>();
        try {
            Class<?> c = Class.forName("org.hibernate.validator.cdi.internal.ValidationExtension");
            result.add((Extension) c.newInstance());
        } catch (NoClassDefFoundError e) {

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {

        }
        try {
            Class<?> c = Class.forName("org.hibernate.validator.internal.cdi.ValidationExtension");
            result.add((Extension) c.newInstance());
        } catch (NoClassDefFoundError e) {

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {

        }

        return result;
    }

    @Override
    public List<Class<? extends Annotation>> extraClassAnnotations() {
        return Arrays.asList(ValidateClasses.class);
    }

    @Override
    public List<Class<?>> handleExtraClassAnnotation(final Annotation annotation, Class<?> c) {
        List<Class<?>> res = new ArrayList<>();
        if(annotation.annotationType().equals(ValidateClasses.class)) {
            Class<?>[] classesToValidateForThis = ((ValidateClasses) annotation).value();
            if(classesToValidateForThis != null) {
                for (Class<?> clazz : classesToValidateForThis) {
                    testExtensionServiceData.get().add(clazz);
                    res.add(clazz);
                }
            }
        }
        return res;
    }


    @Override
    public void preStartupAction(WeldSetupClass weldSetup, Class clazz, Method method) {
        for (Class<?> c : testExtensionServiceData.get()) {
            if(!weldSetup.getBeanClasses().contains(c.getName())) {
                logger.warn("Validation candidate: {} found "
                            + " while scanning availables, but not in testconfiguration included.", c.getSimpleName());
            }
        }
    }


}
