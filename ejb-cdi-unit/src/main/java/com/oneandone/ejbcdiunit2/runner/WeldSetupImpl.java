package com.oneandone.ejbcdiunit2.runner;

import com.oneandone.ejbcdiunit.weldstarter.WeldSetup;
import com.oneandone.ejbcdiunit.weldstarter.WeldSetupBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

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
