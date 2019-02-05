package com.oneandone.iocunit.analyzer;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
abstract public class BaseTest {
    protected Configuration configuration;
    protected List<Class<?>> toBeStarted;

    protected void createTest(Class<?> clazz) {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .theTestClass(clazz);

        ConfigCreator cdiConfigCreator = new ConfigCreator();
        cdiConfigCreator.create(initialConfiguration);
        configuration = cdiConfigCreator.getConfiguration();

        this.toBeStarted = configuration.getObligatory();
    }
}
