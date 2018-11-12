package com.oneandone.ejbcdiunit.weldstarter;

import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Extension;
import java.util.Collection;

/**
 * @author aschoerk
 */
public class WeldStarterTestBase {

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

    WeldContainer getContainer() {
        return weldStarter.container;
    }

    public Instance<Object> getContainerInstance() {
        return getContainer().instance();
    }

    public <T> T selectGet(Class<T> clazz) {
        return getContainerInstance().select(clazz).get();
    }


    WeldSetupClass weldSetup = new WeldSetupClass();
    WeldStarter weldStarter = new WeldStarter();

    @After
    public void tearDown() {
        weldStarter.tearDown();
    }
}
