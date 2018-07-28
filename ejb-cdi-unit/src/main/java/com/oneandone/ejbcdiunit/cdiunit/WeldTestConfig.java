package com.oneandone.ejbcdiunit.cdiunit;

import java.lang.reflect.Method;

import com.oneandone.ejbcdiunit.CdiTestConfig;

/**
 * @author aschoerk
 */
public class WeldTestConfig extends CdiTestConfig {
    Method testMethod;
    Class<?> testClass;

    public WeldTestConfig(Class<?> testClass, Method method) {
        this.testClass = testClass;
        this.testMethod = method;
    }

    public WeldTestConfig(Class<?> testClass, Method method, CdiTestConfig cdiTestConfig) {
        this(testClass, method);
        if (cdiTestConfig != null) {
            this.additionalClasses.addAll(cdiTestConfig.getAdditionalClasses());
            this.additionalClassPackages.addAll(cdiTestConfig.getAdditionalClassPackages());
            this.additionalClassPathes.addAll(cdiTestConfig.getAdditionalClassPathes());
            this.activatedAlternatives.addAll(cdiTestConfig.getActivatedAlternatives());
            this.excludedClasses.addAll(cdiTestConfig.getExcludedClasses());
            this.serviceConfigs.addAll(cdiTestConfig.getServiceConfigs());
            this.setApplicationExceptionDescriptions(cdiTestConfig.getApplicationExceptionDescriptions());
        }
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public Class<?> getTestClass() {
        return testClass;
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

    @Override
    public WeldTestConfig removeClass(Class<?> clazz) {
        super.removeClass(clazz);
        return this;
    }

    @Override
    public WeldTestConfig removeExcluded(Class<?> clazz) {
        super.removeExcluded(clazz);
        return this;
    }

    @Override
    public WeldTestConfig removePackage(Class<?> clazz) {
        super.removePackage(clazz);
        return this;
    }

    @Override
    public WeldTestConfig removeClassPath(Class<?> clazz) {
        super.removeClassPath(clazz);
        return this;
    }

    @Override
    public WeldTestConfig removeAlternative(Class<?> clazz) {
        super.removeAlternative(clazz);
        return this;
    }

    @Override
    public WeldTestConfig removeService(Class<?> clazz) {
        super.removeService(clazz);
        return this;
    }
}
