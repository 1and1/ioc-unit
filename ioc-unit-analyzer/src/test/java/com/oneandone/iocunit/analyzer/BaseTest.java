package com.oneandone.iocunit.analyzer;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
abstract public class BaseTest {
    protected Configuration configuration;
    protected List<Class<?>> toBeStarted;

    protected void createTest(Class<?> clazz) {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .testClass(clazz);

        ConfigCreator cdiConfigCreator = new ConfigCreator();
        cdiConfigCreator.create(initialConfiguration);
        configuration = cdiConfigCreator.getConfiguration();

        this.toBeStarted = configuration.getObligatory();
    }
}
