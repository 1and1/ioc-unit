package com.oneandone.ejbcdiunit.cdiunit;

import java.lang.reflect.Method;

import com.oneandone.ejbcdiunit.CdiTestConfig;

/**
 * @author aschoerk
 */
public class WeldTestConfig extends CdiTestConfig {
    Method method;
    Class<?> clazz;

    public WeldTestConfig(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

    public WeldTestConfig(Class<?> clazz, Method method, CdiTestConfig cdiTestConfig) {
        this(clazz, method);
        if (cdiTestConfig != null) {
            this.additionalClasses.addAll(cdiTestConfig.getAdditionalClasses());
            this.additionalClassPackages.addAll(cdiTestConfig.getAdditionalClassPackages());
            this.additionalClassPathes.addAll(cdiTestConfig.getAdditionalClassPathes());
            this.activatedAlternatives.addAll(cdiTestConfig.getActivatedAlternatives());
            this.excludedClasses.addAll(cdiTestConfig.getExcludedClasses());
            this.serviceConfigs.addAll(cdiTestConfig.getServiceConfigs());
        }
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public WeldTestConfig addClass(Class<?> clazz) {
        super.addClass(clazz);
        return this;
    }

    @Override
    public WeldTestConfig addPackage(Class<?> clazz) {
        super.addPackage(clazz);
        return this;
    }

    @Override
    public WeldTestConfig addClassPath(Class<?> clazz) {
        super.addClassPath(clazz);
        return this;
    }

    @Override
    public WeldTestConfig addAlternative(Class<?> clazz) {
        super.addAlternative(clazz);
        return this;
    }

    @Override
    public WeldTestConfig addExcluded(Class<?> clazz) {
        super.addExcluded(clazz);
        return this;
    }

    @Override
    public WeldTestConfig addServiceConfig(ServiceConfig serviceConfig) {
        super.addServiceConfig(serviceConfig);
        return this;
    }
}
