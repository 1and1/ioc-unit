package com.oneandone.cdi.weldstarter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.enterprise.inject.spi.Extension;

import org.junit.After;
import org.junit.Before;

import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * Helps develop tests to verify behaviour of CDI using weld-starters
 *
 * @author aschoerk
 */
public class WeldStarterTestBase {

    @Before
    public void initWeldStarter() {
        ServiceLoader<WeldStarter> loader = ServiceLoader.load(WeldStarter.class);
        List<WeldStarter> starters = new ArrayList<>();
        for (Iterator<WeldStarter> it = loader.iterator(); it.hasNext();) {
            WeldStarter s = it.next();
            starters.add(s);
        }
        if (starters.size() == 0)
            throw new RuntimeException("No WeldStarter found");
        if (starters.size() > 1)
            throw new RuntimeException("More than one WeldStarter found");
        setWeldStarter(starters.get(0));
    }

    public WeldSetupClass getWeldSetup() {
        return weldSetup;
    }

    public void setWeldSetup(final WeldSetupClass weldSetup) {
        this.weldSetup = weldSetup;
    }

    WeldSetupClass weldSetup = new WeldSetupClass();
    WeldStarter weldStarter = null;

    public void setWeldStarter(WeldStarter weldStarterP) {
        this.weldStarter = weldStarterP;
    }

    public void setBeanClasses(Class... classes) {
        weldSetup.setBeanClasses(classes);
    }

    public void setDecoratorClasses(Class<?>... classes) {
        weldSetup.setEnabledDecorators(Arrays.asList(classes));
    }

    public void setInterceptorClasses(Class<?>... classes) {
        weldSetup.setEnabledInterceptors((Collection<Class<?>>) Arrays.asList(classes));
    }


    public void addBeanClasses(Class... classes) {
        weldSetup.addBeanClasses(classes);
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

    public String getStarterClassname() {
        return weldStarter.getClass().getName();
    }

    @After
    public void tearDown() {
        if (weldStarter != null)
            weldStarter.tearDown();
    }
}
