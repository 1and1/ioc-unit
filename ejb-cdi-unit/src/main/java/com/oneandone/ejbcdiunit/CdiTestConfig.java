package com.oneandone.ejbcdiunit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.util.reflection.Formats;

import com.oneandone.ejbcdiunit.internal.ApplicationExceptionDescription;

/**
 * @author aschoerk
 */
public class CdiTestConfig {

    public String weldVersion = Formats.version(WeldBootstrap.class.getPackage());
    protected Set<Class<?>> additionalClasses = new HashSet<>();
    protected Set<Class<?>> additionalClassPathes = new HashSet<>();
    protected Set<Class<?>> additionalClassPackages = new HashSet<>();
    protected Set<Class<?>> excludedClasses = new HashSet<>();
    protected Set<Class<?>> activatedAlternatives = new HashSet<>();
    protected Set<ServiceConfig> serviceConfigs = new HashSet<>();
    private List<ApplicationExceptionDescription> applicationExceptionDescriptions = new ArrayList<>();

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

    public CdiTestConfig removeClass(Class<?> clazz) {
        additionalClasses.remove(clazz);
        return this;
    }

    public CdiTestConfig removeExcluded(Class<?> clazz) {
        excludedClasses.remove(clazz);
        return this;
    }

    public CdiTestConfig removePackage(Class<?> clazz) {
        additionalClassPackages.remove(clazz);
        return this;
    }

    public CdiTestConfig removeClassPath(Class<?> clazz) {
        additionalClassPathes.remove(clazz);
        return this;
    }

    public CdiTestConfig removeAlternative(Class<?> clazz) {
        activatedAlternatives.remove(clazz);
        return this;
    }

    public CdiTestConfig removeService(Class<?> clazz) {
        serviceConfigs.remove(new ServiceConfig(clazz, null));
        return this;
    }

    public List<ApplicationExceptionDescription> getApplicationExceptionDescriptions() {
        return applicationExceptionDescriptions;
    }

    public void setApplicationExceptionDescriptions(List<ApplicationExceptionDescription> applicationExceptionDescriptions) {
        this.applicationExceptionDescriptions = applicationExceptionDescriptions;
    }

    public void addExcludedByString(String s) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if ((o == null) || (!getClass().equals(o.getClass()))) {
                return false;
            }

            ServiceConfig<?> that = (ServiceConfig<?>) o;

            return getServiceClass().equals(that.getServiceClass());
        }

        @Override
        public int hashCode() {
            return getServiceClass().hashCode();
        }
    }

}
