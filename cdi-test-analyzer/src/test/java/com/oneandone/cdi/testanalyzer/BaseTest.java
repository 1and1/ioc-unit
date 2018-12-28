package com.oneandone.cdi.testanalyzer;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BaseTest {
    Configuration createTest(Class<?> clazz) {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .testClass(clazz);

        ConfigCreator cdiConfigCreator = new ConfigCreator();
        cdiConfigCreator.create(initialConfiguration);
        return cdiConfigCreator.getConfiguration();
    }
}
