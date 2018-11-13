package com.oneandone.cdi.weldstarter;

import java.util.Collection;

import javax.enterprise.inject.spi.Extension;

import org.junit.After;

/**
 * @author aschoerk
 */
public class WeldStarterTestBase {
    WeldSetupClass weldSetup = new WeldSetupClass();
    WeldStarter weldStarter = null;

    public void setWeldStarter(WeldStarter weldStarterP) {
        this.weldStarter = weldStarterP;
    }

    public void setBeanClasses(Class... classes) {
        weldSetup.setBeanClasses(classes);
    }

    public void setExtensions(Collection<Class<? extends Extension>> classes) {
        weldSetup.setExtensions(classes);
    }

    public void setBeanClasses(Collection<Class<?>> classes) {
        weldSetup.setBeanClasses(classes.toArray(new Class<?>[classes.size()]));
    }
    public void setAlternativeClasses(Collection<Class<?>> classes) {
        weldSetup.setAlternativeClasses(classes.toArray(new Class<?>[classes.size()]));
    }

    public void setAlternativeClasses(Class... classes) {
        weldSetup.setAlternativeClasses(classes);
    }

    public void setEnabledAlternativeStereotypes(Class... classes) {
        weldSetup.setEnabledAlternativeStereotypes(classes);
    }

    public void start() {
        weldStarter.start(weldSetup);
    }

    public <T> T selectGet(Class<T> clazz) {
        return weldStarter.get(clazz);
    }

    @After
    public void tearDown() {
        weldStarter.tearDown();
    }
}
