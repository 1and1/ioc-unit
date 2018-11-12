package com.oneandone.ejbcdiunit.weldstarter;

import java.util.ArrayList;

public class WeldSetupClass extends WeldSetupBase implements WeldSetup {

    public void setBeanClasses(Class... classes) {
        beanClasses = new ArrayList<>();
        for (Class clazz : classes) {
            beanClasses.add(clazz.getName());
        }
    }



    public void setAlternativeClasses(Class... classes) {
        this.alternativeClasses = new ArrayList<>();
        for (Class clazz : classes) {
            addAlternativeClass(clazz);
        }
    }




    public void setEnabledAlternativeStereotypes(Class... classes) {
        this.enabledAlternativeStereotypes = new ArrayList<>();
        for (Class clazz : classes) {
            addEnabledAlternativeStereotype(clazz);
        }
    }





}
