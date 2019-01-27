package com.oneandone.iocunit.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class Phase5Warner extends PhasesBase {
    Logger logger = LoggerFactory.getLogger(Phase5Warner.class);
    private final InitialConfiguration initial;

    public Phase5Warner(InitialConfiguration initial, final Configuration configuration) {
        super(configuration);
        this.initial = initial;
    }

    public void work() {
        configuration.setPhase(Configuration.Phase.WARNING);
        checkApplicationScoped();
        checkProducersInSuperClass();
        outputUnresolvedInjects();
        configuration.setPhase(Configuration.Phase.UNKNOWN);
    }

    private void outputUnresolvedInjects() {

        for (QualifiedType i: configuration.getInjects()) {
            if (ConfigStatics.mightBeBean(i.getRawtype())) {
                // TODO: exclude configuration of primitives by Injects
                // perhaps better would be to better interpret InjectionPoint - parameter
                logger.error("Unresolved Inject: {}", i);
            }
        }
    }

    private void checkProducersInSuperClass(Class<?> c) {
        if (c.equals(Object.class)) {
            return;
        } else {
            for (Field f: c.getDeclaredFields()) {
                if (f.getAnnotation(Produces.class) != null)  {
                    logger.warn("Producer Field {} in Superclass: {}.",f.getName(), c.getName());
                }
            }
            for (Method m: c.getDeclaredMethods()) {
                if (m.getAnnotation(Produces.class) != null)  {
                    logger.warn("Producer Method {} in Superclass: {}.",m.getName(), c.getName());
                }
            }
            checkProducersInSuperClass(c.getSuperclass());
        }
    }

    private void checkProducersInSuperClass() {
        for (Class<?> c: configuration.getObligatory()) {
            if (ConfigStatics.mightBeBean(c)) {
                if (c.getAnnotation(Alternative.class) != null || c.equals(configuration.getTestClass())){
                    continue;
                } else {
                    checkProducersInSuperClass(c.getSuperclass());
                }
            }
        }
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
        InjectFinder injectFinder = new InjectFinder(configuration);
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
