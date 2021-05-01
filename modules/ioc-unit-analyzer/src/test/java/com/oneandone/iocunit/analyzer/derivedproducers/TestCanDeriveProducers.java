package com.oneandone.iocunit.analyzer.derivedproducers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.ConfigCreator;
import com.oneandone.iocunit.analyzer.InitialConfiguration;

/**
 * @author aschoerk
 */
public class TestCanDeriveProducers extends BaseTest {
    @Test
    public void canIgnoreProducerInSuperClass() {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .sutClass(BeanContainerSub.class)
                .sutClass(BeanInjector.class);

        ConfigCreator creator = new ConfigCreator();
        creator.create(initialConfiguration);

        assertTrue(creator.getConfiguration().getObligatory().contains(Bean.class));
    }

    @Test
    public void canDetectProducerInSuperOfTestClass() {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .testClass(BeanContainerSub.class)
                .sutClass(BeanInjector.class)
                .setTestClass(BeanContainerSub.class);

        ConfigCreator creator = new ConfigCreator();
        creator.create(initialConfiguration);

        assertFalse(creator.getConfiguration().getObligatory().contains(Bean.class));
    }

    @Test
    public void canDetectProducerInSuperOfAlternativeClass() {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .sutClass(BeanContainerAlternativeSub.class)
                .sutClass(BeanInjector.class);

        ConfigCreator creator = new ConfigCreator();
        creator.create(initialConfiguration);

        assertFalse(creator.getConfiguration().getObligatory().contains(Bean.class));
    }

}
