package com.oneandone.cdi.weldstarter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import javax.enterprise.inject.spi.Extension;

import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * extends WeldSetupBase to additionally support finding the starter. Also adds some convenience methods.
 */
public class WeldSetupClass extends WeldSetupBase implements WeldSetup {

    /**
     * Returns the weldstarter as configured by maven. If there are more than one the starter for the highest version of weld is returned.
     *
     * @return a weld starter as configured by dependencies.
     */
    public static WeldStarter getWeldStarter() {
        ServiceLoader<WeldStarter> loader = ServiceLoader.load(WeldStarter.class);
        List<WeldStarter> starters = new ArrayList<>();
        for (Iterator<WeldStarter> it = loader.iterator(); it.hasNext();) {
            WeldStarter s = it.next();
            starters.add(s);
        }
        if (starters.size() == 0)
            throw new RuntimeException("No WeldStarter found");
        if (starters.size() > 1) {
            Optional<WeldStarter> tostart = starters.stream().max((s, t) -> s.getClass().getName().compareTo(t.getClass().getName()));
            return tostart.get();
        }
        return starters.get(0);
    }

    public static boolean isWeld3() {
        return getWeldStarter().getClass().getName().contains("weld3");
    }

    public static boolean isWeld2() {
        return getWeldStarter().getClass().getName().contains("weld2");
    }

    public static boolean isWeld1() {
        return getWeldStarter().getClass().getName().contains("weld1");
    }

    public void setBeanClasses(Class... classes) {
        beanClasses = new ArrayList<>();
        for (Class clazz : classes) {
            beanClasses.add(clazz.getName());
        }
    }

    public void setBeanClasses(Collection<Class<?>> classes) {
        beanClasses = new ArrayList<>();
        for (Class clazz : classes) {
            beanClasses.add(clazz.getName());
        }
    }

    public void setBeanClassNames(Collection<String> classes) {
        beanClasses = new ArrayList<>();
        for (String className : classes) {
            beanClasses.add(className);
        }
    }


    public void addBeanClasses(Class... classes) {
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

    public void setAlternativeClasses(Collection<Class<?>> classes) {
        this.alternativeClasses = new ArrayList<>();
        for (Class clazz : classes) {
            addAlternativeClass(clazz);
        }
    }


    public void addAlternativeClasses(Class... classes) {
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

    public void setEnabledAlternativeStereotypeNames(Collection<String> classNames) {
        this.enabledAlternativeStereotypes = new ArrayList<>();
        for (String className : classNames) {
            addEnabledAlternativeStereotype(className);
        }
    }

    public void setExtensionObjects(final Collection<Extension> extensionsP) {
        for (Extension extension : extensionsP) {
            this.extensions.add(new MetadataImpl<>(extension, ""));
        }
    }
}
