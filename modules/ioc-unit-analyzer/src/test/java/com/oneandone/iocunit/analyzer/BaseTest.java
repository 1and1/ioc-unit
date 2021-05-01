package com.oneandone.iocunit.analyzer;

import java.util.List;

import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

abstract public class BaseTest {
    protected Configuration configuration;
    protected List<Class<?>> toBeStarted;

    protected void createTest(Class<?> clazz) {
        createTest(clazz, clazz, true);
    }

    protected void createTest(Class<?> clazz, boolean doStartWeld) {
        createTest(clazz, clazz, doStartWeld);
    }

    protected void createTest(Class<?> clazz, Class<?> sutClass) {
        createTest(clazz, sutClass, true);
    }

    protected void createTest(Class<?> clazz, Class<?> sutClass, boolean doStartWeld) {
        InitialConfiguration initialConfiguration
                = new InitialConfiguration()
                .theTestClass(clazz)
                .sutClass(sutClass);

        ConfigCreator cdiConfigCreator = new ConfigCreator();
        cdiConfigCreator.create(initialConfiguration);
        configuration = cdiConfigCreator.getConfiguration();

        this.toBeStarted = configuration.getObligatory();
        if(doStartWeld) {

            WeldStarter weldStarter = WeldSetupClass.getWeldStarter();
            WeldSetupClass setup = new SetupCreator(configuration).buildWeldSetup(null);
            weldStarter.start(setup);
            BaseClass res = (BaseClass) weldStarter.get(clazz);
            res.init();
            weldStarter.tearDown();
        }
    }

}
