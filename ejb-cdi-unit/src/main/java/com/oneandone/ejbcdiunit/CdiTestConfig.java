package com.oneandone.ejbcdiunit;

import java.util.HashSet;
import java.util.Set;

import org.jboss.weld.bootstrap.api.Service;

/**
 * @author aschoerk
 */
public class CdiTestConfig {

    protected Set<Class<?>> additionalClasses = new HashSet<>();
    protected Set<Class<?>> additionalClassPathes = new HashSet<>();
    protected Set<Class<?>> additionalClassPackages = new HashSet<>();
    protected Set<Class<?>> excludedClasses = new HashSet<>();
    protected Set<Class<?>> activatedAlternatives = new HashSet<>();
    protected Set<ServiceConfig> serviceConfigs = new HashSet<>();

    public Set<Class<?>> getExcludedClasses() {
        return excludedClasses;
    }

    public Set<Class<?>> getAdditionalClasses() {
        return additionalClasses;
    }

    public Set<Class<?>> getAdditionalClassPathes() {
        return additionalClassPathes;
    }

    public Set<Class<?>> getAdditionalClassPackages() {
        return additionalClassPackages;
    }

    public Set<Class<?>> getActivatedAlternatives() {
        return activatedAlternatives;
    }

    public Set<ServiceConfig> getServiceConfigs() {
        return serviceConfigs;
    }

    public CdiTestConfig addClass(Class<?> clazz) {
        additionalClasses.add(clazz);
        return this;
    }

    public CdiTestConfig addExcluded(Class<?> clazz) {
        excludedClasses.add(clazz);
        return this;
    }

    public CdiTestConfig addPackage(Class<?> clazz) {
        additionalClassPackages.add(clazz);
        return this;
    }

    public CdiTestConfig addClassPath(Class<?> clazz) {
        additionalClassPathes.add(clazz);
        return this;
    }

    public CdiTestConfig addAlternative(Class<?> clazz) {
        activatedAlternatives.add(clazz);
        return this;
    }

    public CdiTestConfig addServiceConfig(ServiceConfig serviceConfig) {
        serviceConfigs.add(serviceConfig);
        return this;
    }

    public static class ServiceConfig<S extends Service> {
        Class<S> serviceClass;
        S service;

        public ServiceConfig(Class<S> serviceClass, S service) {
            this.serviceClass = serviceClass;
            this.service = service;
        }

        public Class<? extends Service> getServiceClass() {
            return serviceClass;
        }

        public void setServiceClass(Class<S> serviceClassP) {
            this.serviceClass = serviceClassP;
        }

        public S getService() {
            return service;
        }

        public void setService(S serviceP) {
            this.service = serviceP;
        }
    }

}
