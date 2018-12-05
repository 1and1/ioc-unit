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

import com.oneandone.cdi.tester.ejb.ApplicationExceptionDescription;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * @author aschoerk
 */
public class CdiTestConfig {

    private final WeldStarter weldStarter;

    public CdiTestConfig(Class<?> testClass, Method method, WeldStarter weldStarter) {
        this(weldStarter);
        this.testClass = testClass;
        this.testMethod = method;
    }

    public CdiTestConfig(Class<?> testClass, Method method, CdiTestConfig cdiTestConfig, WeldStarter weldStarter) {
        this(testClass, method, weldStarter);
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

    public List<ApplicationExceptionDescription> getApplicationExceptionDescriptions() {
        return applicationExceptionDescriptions;
    }

    public void setApplicationExceptionDescriptions(final List<ApplicationExceptionDescription> applicationExceptionDescriptions) {
        this.applicationExceptionDescriptions = applicationExceptionDescriptions;
    }

    Method testMethod;
    Class<?> testClass;

    protected Set<Class<?>> additionalClasses = new HashSet<>();
    protected Set<Class<?>> additionalClassPathes = new HashSet<>();
    protected Set<Class<?>> additionalClassPackages = new HashSet<>();
    protected Set<Class<?>> excludedClasses = new HashSet<>();
    protected Set<Class<?>> activatedAlternatives = new HashSet<>();
    protected Set<ServiceConfig> serviceConfigs = new HashSet<>();
    private List<ApplicationExceptionDescription> applicationExceptionDescriptions = new ArrayList<>();

    public CdiTestConfig() {
        this.weldStarter = WeldSetupClass.getWeldStarter();
    }


    public CdiTestConfig(WeldStarter weldStarter) {
        this.weldStarter = weldStarter;

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

    public void addExcludedByString(String s) {
    }

    /*
     * TestConfig Part
     */

    private Collection<Class<?>> alternatives = new ArrayList<>();
    private Class<?> ejbJarClasspathExample = null;
    private Collection<Extension> extensions = new ArrayList<Extension>();
    private Collection<Class<?>> enabledInterceptors = new ArrayList<>();
    private Collection<Class<?>> enabledDecorators = new ArrayList<>();
    private Collection<String> enabledAlternativeStereotypes = new ArrayList<String>();
    private Set<URL> classpathEntries = new HashSet<>();
    private Set<String> discoveredClasses = new LinkedHashSet<String>();

    public Set<String> getDiscoveredClasses() {
        return discoveredClasses;
    }

    public Collection<Class<?>> getAlternatives() {
        return alternatives;
    }

    public Class<?> getEjbJarClasspathExample() {
        return ejbJarClasspathExample;
    }

    public void setEjbJarClasspathExample(final Class<?> ejbJarClasspathExampleP) {
        this.ejbJarClasspathExample = ejbJarClasspathExampleP;
    }

    public Collection<Extension> getExtensions() {
        return extensions;
    }

    public Collection<Class<?>> getEnabledInterceptors() {
        return enabledInterceptors;
    }

    public Collection<Class<?>> getEnabledDecorators() {
        return enabledDecorators;
    }

    public Collection<String> getEnabledAlternativeStereotypes() {
        return enabledAlternativeStereotypes;
    }

    public Set<URL> getClasspathEntries() {
        return classpathEntries;
    }

    public CharSequence getWeldVersion() {
        return weldStarter.getVersion();
    }

    public static class ServiceConfig<S> {
        Class<S> serviceClass;
        S service;

        public ServiceConfig(Class<S> serviceClass, S service) {
            this.serviceClass = serviceClass;
            this.service = service;
        }

        public Class<?> getServiceClass() {
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
