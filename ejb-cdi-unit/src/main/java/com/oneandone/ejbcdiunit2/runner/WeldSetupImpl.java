package com.oneandone.ejbcdiunit2.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupBase;

public class WeldSetupImpl extends WeldSetupBase implements WeldSetup {
    public void setBeanClasses(Collection<Class<?>> classes) {
        beanClasses = new ArrayList<>();
        for (Class clazz : classes) {
            beanClasses.add(clazz.getName());
        }
    }

    public void setAlternativeClasses(Set<Class<?>> enabledAlternatives) {
        for (Class<?> clazz: enabledAlternatives) {
            addAlternativeClass(clazz);
        }
    }

    public void setEnabledAlternativeStereotypes(Set<Class<?>> enabledAlternatives) {
        for (Class<?> clazz: enabledAlternatives) {
            addAlternativeClass(clazz);
        }
    }
}
