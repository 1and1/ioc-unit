package com.oneandone.iocunit.analyzer.derivedproducers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.ConfigCreator;
import com.oneandone.iocunit.analyzer.InitialConfiguration;
import com.oneandone.iocunit.analyzer.LogbackFilter;

/**
 * @author aschoerk
 */
public class TestPhase5WarnerCanDetectedDerivedProducers extends BaseTest {
    private void initLogbackFilter() {
        LogbackFilter.clear();
        LogbackFilter.doSaveMessages();
    }

    @BeforeEach
    public void setup() {
        initLogbackFilter();
    }

    @Test
    public void canDetectProducerInDerivedClass() {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .testClass(BeanContainerSub.class);

        ConfigCreator creator = new ConfigCreator();
        creator.create(initialConfiguration);

        assertTrue(LogbackFilter.getMessages().contains("Phase5Warner"));
        assertTrue(LogbackFilter.getMessages().contains("Producers in Superclass:"));
        assertTrue(LogbackFilter.getMessages().contains("BeanContainer: b"));
    }

    @Test
    public void canIgnoreDerivedProducersInTestClass() {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .testClass(BeanContainerSub.class);

        initialConfiguration.setTestClass(BeanContainerSub.class);
        ConfigCreator creator = new ConfigCreator();
        creator.create(initialConfiguration);

        assertFalse(LogbackFilter.getMessages().contains("Phase5Warner"));
    }

    @Test
    public void canIgnorederivedProducersInAlternativeClass() {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .testClass(BeanContainerAlternativeSub.class);

        ConfigCreator creator = new ConfigCreator();
        creator.create(initialConfiguration);

        assertFalse(LogbackFilter.getMessages().contains("Phase5Warner"));
    }

}
