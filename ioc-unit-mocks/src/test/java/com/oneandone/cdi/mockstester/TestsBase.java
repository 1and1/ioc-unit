package com.oneandone.cdi.mockstester;

import java.net.MalformedURLException;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.oneandone.iocunit.analyzer.ConfigCreator;
import com.oneandone.iocunit.analyzer.InitialConfiguration;
import com.oneandone.iocunit.analyzer.SetupCreator;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class TestsBase extends WeldStarterTestBase {
    @BeforeEach
    public void beforeEach() {
        cfg = new InitialConfiguration();
    }

    InitialConfiguration cfg = new InitialConfiguration();

    public void initialClasses(Class<?>... classes) {
        cfg.initialClasses.addAll(Arrays.asList(classes));
    }

    public void enabledAlternatives(Class<?>... classes) {
        cfg.enabledAlternatives.addAll(Arrays.asList(classes));
    }

    public void testClass(Class clazz) {
        cfg.testClass = clazz;
    }


    @AfterEach
    public void afterEach() {
        tearDown();
    }


    protected void configureAndStart() throws MalformedURLException {
        initWeldStarter();
        ConfigCreator configCreator = new ConfigCreator();
        configCreator.create(cfg);
        WeldSetupClass res = new SetupCreator(configCreator.getConfiguration()).buildWeldSetup(null);
        setWeldSetup(res);
        // throw new RuntimeException();
        // setBeanClasses(configCreator.toBeStarted());
        // setEnabledAlternativeStereotypes(ProducesAlternative.class);
        // setAlternativeClasses(configCreator.getEnabledAlternatives());
        // setExtensions(configCreator.getExtensions());
        start();
    }


    static class DummyBean {

    }


}
