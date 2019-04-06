package com.oneandone.iocunit.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

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
            logger.error("Unresolved Inject: {}", i);
        }
    }


    private Set<Type> collectProducedClasses(Class<?> c) {
        Set<Type> res = new HashSet<>();
        for (Field f: c.getDeclaredFields()) {
            if (f.getAnnotation(Produces.class) != null)  {
                res.add(f.getGenericType());
            }
        }
        for (Method m: c.getDeclaredMethods()) {
            if (m.getAnnotation(Produces.class) != null)  {
                res.add(m.getGenericReturnType());
            }
        }
        return res;

    }

    private void checkProducersInSuperClass(Class<?> c, Set<Type> producedInSubClass) {
        if (c.equals(Object.class)) {
            return;
        } else {
            StringBuffer producers = new StringBuffer();
            for (Field f: c.getDeclaredFields()) {
                if (f.getAnnotation(Produces.class) != null && !producedInSubClass.contains(f.getGenericType()))  {
                    if (producers.length() > 0) producers.append(",");
                    producers.append(f.getName());
                }
            }
            for (Method m: c.getDeclaredMethods()) {
                if (m.getAnnotation(Produces.class) != null && !producedInSubClass.contains(m.getGenericReturnType()))  {
                    if (producers.length() > 0) producers.append(",");
                    producers.append(m.getName());
                }
            }
            if (producers.length() > 0) {
                logger.warn("Producers in Superclass: {}: {}",c.getSimpleName(), producers);
            }
            checkProducersInSuperClass(c.getSuperclass(), producedInSubClass);
        }
    }

    private void checkProducersInSuperClass() {
        for (Class<?> c: configuration.getObligatory()) {
            if (ConfigStatics.mightBeBean(c)) {
                if (c.getAnnotation(Alternative.class) != null || c.equals(configuration.getTheTestClass())){
                    continue;
                } else {
                    checkProducersInSuperClass(c.getSuperclass(), collectProducedClasses(c));
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
