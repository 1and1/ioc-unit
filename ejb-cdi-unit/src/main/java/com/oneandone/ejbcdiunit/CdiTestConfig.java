package com.oneandone.ejbcdiunit;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.util.reflection.Formats;

import com.oneandone.ejbcdiunit.internal.ApplicationExceptionDescription;

/**
 * @author aschoerk
 */
public class CdiTestConfig {

    public CdiTestConfig(Class<?> testClass, Method method) {
        this.testClass = testClass;
        this.testMethod = method;
    }

    public CdiTestConfig(Class<?> testClass, Method method, CdiTestConfig cdiTestConfig) {
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

    Method testMethod;
    Class<?> testClass;

    public String weldVersion = Formats.version(WeldBootstrap.class.getPackage());
    protected Set<Class<?>> additionalClasses = new HashSet<>();
    protected Set<Class<?>> additionalClassPathes = new HashSet<>();
    protected Set<Class<?>> additionalClassPackages = new HashSet<>();
    protected Set<Class<?>> excludedClasses = new HashSet<>();
    protected Set<Class<?>> activatedAlternatives = new HashSet<>();
    protected Set<ServiceConfig> serviceConfigs = new HashSet<>();
    private List<ApplicationExceptionDescription> applicationExceptionDescriptions = new ArrayList<>();

    public CdiTestConfig() {

    }


    public Method getTestMethod() {
        return testMethod;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

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

    public List<ApplicationExceptionDescription> getApplicationExceptionDescriptions() {
        return applicationExceptionDescriptions;
    }

    public void setApplicationExceptionDescriptions(List<ApplicationExceptionDescription> applicationExceptionDescriptions) {
        this.applicationExceptionDescriptions = applicationExceptionDescriptions;
    }

    public void addExcludedByString(String s) {
    }

    /*
     * TestConfig Part
     */

    private Collection<Metadata<String>> alternatives = new ArrayList<Metadata<String>>();
    private Class<?> ejbJarClasspathExample = null;
    private Collection<Metadata<? extends Extension>> extensions = new ArrayList<Metadata<? extends Extension>>();
    private Collection<Metadata<String>> enabledInterceptors = new ArrayList<Metadata<String>>();
    private Collection<Metadata<String>> enabledDecorators = new ArrayList<Metadata<String>>();
    private Collection<Metadata<String>> enabledAlternativeStereotypes = new ArrayList<Metadata<String>>();
    private Set<URL> classpathEntries = new HashSet<>();
    private Set<String> discoveredClasses = new LinkedHashSet<String>();

    public Set<String> getDiscoveredClasses() {
        return discoveredClasses;
    }

    public Collection<Metadata<String>> getAlternatives() {
        return alternatives;
    }

    public Class<?> getEjbJarClasspathExample() {
        return ejbJarClasspathExample;
    }

    public void setEjbJarClasspathExample(final Class<?> ejbJarClasspathExample) {
        this.ejbJarClasspathExample = ejbJarClasspathExample;
    }

    public Collection<Metadata<? extends Extension>> getExtensions() {
        return extensions;
    }

    public Collection<Metadata<String>> getEnabledInterceptors() {
        return enabledInterceptors;
    }

    public Collection<Metadata<String>> getEnabledDecorators() {
        return enabledDecorators;
    }

    public Collection<Metadata<String>> getEnabledAlternativeStereotypes() {
        return enabledAlternativeStereotypes;
    }

    public Set<URL> getClasspathEntries() {
        return classpathEntries;
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
