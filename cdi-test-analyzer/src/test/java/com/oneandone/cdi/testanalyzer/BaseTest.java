package com.oneandone.cdi.testanalyzer;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Set;

@RunWith(JUnit4.class)
abstract public class BaseTest {
    protected Configuration configuration;
    protected Set<Class<?>> toBeStarted;

    protected void createTest(Class<?> clazz) {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .testClass(clazz);

        ConfigCreator cdiConfigCreator = new ConfigCreator();
        cdiConfigCreator.create(initialConfiguration);
        configuration = cdiConfigCreator.getConfiguration();

        this.toBeStarted = configuration.getToBeStarted();
    }
}
