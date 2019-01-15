package com.oneandone.iocunit.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class Phase4Warner extends PhasesBase {
    Logger logger = LoggerFactory.getLogger(Phase4Warner.class);
    private final InitialConfiguration initial;

    public Phase4Warner(InitialConfiguration initial, final Configuration configuration) {
        super(configuration);
        this.initial = initial;
    }

    public void work() {
        configuration.setPhase(Configuration.Phase.WARNING);
        checkApplicationScoped();
        configuration.setPhase(Configuration.Phase.UNKNOWN);

    }

    private void checkApplicationScoped() {
        // normally should check test class as well. Since this normally is not used as injected type
        // no problem here.
        // if (initial.testClass != null)
        //     checkInjectedFieldsOfApplicationScoped(initial.testClass);
        for (Class<?> b: configuration.getObligatory()) {
            if (b.getAnnotation(ApplicationScoped.class) != null) {
                if (!isTestClassOrSuper(b))
                    checkInjectedFieldsOfApplicationScoped(b);
            }
        }
    }

    private boolean isTestClassOrSuper(final Class<?> b) {
        Class<?> testClass = initial.testClass;
        while (testClass != null && !testClass.equals(Object.class)) {
            if (b.equals(testClass))
                return true;
            testClass = testClass.getSuperclass();
        }
        return false;
    }

    private void checkInjectedFieldsOfApplicationScoped(final Class<?> aClass) {
        if (aClass.equals(Object.class))
            return;
        InjectFinder injectFinder = new InjectFinder(configuration.testerExtensionsConfigsFinder);
        injectFinder.find(aClass);
        for (QualifiedType i : injectFinder.getInjectedTypes()) {
            if (i.isField()) {
                Field f = i.getField();
                if (f.getAnnotation(Inject.class) == null
                    || Modifier.isPrivate(f.getModifiers()) || Modifier.isProtected(f.getModifiers())) {
                    ; // ok
                } else {
                    logger.warn("ApplicationScoped class {} has non private injected field {}",aClass, f);
                }
            }
        }
        checkInjectedFieldsOfApplicationScoped(aClass.getSuperclass());
    }
}
