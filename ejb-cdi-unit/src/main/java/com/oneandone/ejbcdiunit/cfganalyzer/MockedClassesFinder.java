package com.oneandone.ejbcdiunit.cfganalyzer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.mockito.Mock;

/**
 * @author aschoerk
 */
public class MockedClassesFinder {

    public Set<Class<?>> findMockedClassesOfTest(Class<?> testClass) {
        Set<Class<?>> mockedClasses = new HashSet<Class<?>>();
        Class<?> actClass = testClass;
        while (!actClass.equals(Object.class)) {
            findMockedClassesOfTest(actClass, mockedClasses);
            actClass = actClass.getSuperclass();
        }
        return mockedClasses;
    }

    private Set<Class<?>> findMockedClassesOfTest(Class<?> testClass, Set<Class<?>> mockedClasses) {

        try {
            for (Field field : testClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Mock.class)) {
                    Class<?> type = field.getType();
                    mockedClasses.add(type);
                }
            }
        } catch (NoClassDefFoundError e) {

        }

        try {

            for (Field field : testClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(org.easymock.Mock.class)) {
                    Class<?> type = field.getType();
                    mockedClasses.add(type);
                }
            }
        } catch (NoClassDefFoundError e) {

        }
        return mockedClasses;
    }
}
