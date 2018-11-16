package com.oneandone.cdi.weldstarter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.DeploymentException;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.Metadata;

public class WeldSetupBase implements WeldSetup {
    protected Collection<String> beanClasses = Collections.EMPTY_LIST;
    protected List<Metadata<String>> alternativeClasses = Collections.EMPTY_LIST;
    protected List<Metadata<String>> enabledAlternativeStereotypes = Collections.EMPTY_LIST;
    protected List<Metadata<String>> enabledInterceptors = Collections.EMPTY_LIST;
    protected List<Metadata<String>> enabledDecorators = Collections.EMPTY_LIST;
    protected List<WeldSetup.ServiceConfig> services = new ArrayList<>();
    protected List<Metadata<Extension>> extensions = new ArrayList<>();

    @Override
    public List<ServiceConfig> getServices() {
        return services;
    }

    @Override
    public List<Metadata<String>> getEnabledInterceptors() {
        return enabledInterceptors;
    }

    @Override
    public List<Metadata<String>> getEnabledDecorators() {
        return enabledDecorators;
    }

    @Override
    public Collection<String> getBeanClasses() {
        return beanClasses;
    }

    @Override
    public List<Metadata<String>> getAlternativeClasses() {
        return alternativeClasses;
    }

    @Override
    public List<Metadata<String>> getEnabledAlternativeStereotypes() {
        return enabledAlternativeStereotypes;
    }

    @Override
    public void setDeploymentException(DeploymentException deploymentException) {

    }
    @Override
    public Iterable<Metadata<Extension>> getExtensions() {
        return extensions;
    }

    @Override
    public void registerServices(ServiceRegistry serviceRegistry) {
        for (WeldSetup.ServiceConfig sc: services) {
            serviceRegistry.add(sc.serviceClass, sc.service);
        }
    }

    public void setServices(List<ServiceConfig> services) {
        this.services = services;
    }

    public void addAlternativeClass(Class clazz) {
        alternativeClasses.add(new StringMetadata(clazz, "Alternative Class"));
    }

    public void addEnabledAlternativeStereotype(Class clazz) {
        enabledAlternativeStereotypes.add(new StringMetadata(clazz, "Alternative Stereotype"));
    }

    public void addEnabledAlternativeStereotype(String className) {
        enabledAlternativeStereotypes.add(new StringMetadata(className, "Alternative Stereotype"));
    }

    public void addEnabledInterceptor(Class<?> clazz) {
        enabledInterceptors.add(new StringMetadata(clazz, "Interceptor"));
    }

    public void setEnabledInterceptors(Collection<Class<?>> classes) {
        enabledInterceptors = new ArrayList<>();
        for (Class clazz : classes) {
            enabledInterceptors.add(new StringMetadata(clazz, "Interceptor"));
        }
    }

    public void addEnabledDecorator(Class<?> clazz) {
        enabledDecorators.add(new StringMetadata(clazz, "Decorator"));
    }

    public void setEnabledDecorators(Collection<Class<?>> classes) {
        enabledDecorators = new ArrayList<>();
        for (Class clazz : classes) {
            enabledDecorators.add(new StringMetadata(clazz, "Decorator"));
        }
    }

    protected void addExtension(Class<? extends Extension> clazz) {
        try {
            this.extensions.add(new MetadataImpl<Extension>(clazz.newInstance(), "Alternative In Testcode"));
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <S extends Service> void addService(ServiceConfig<S> serviceConfig) {
        services.add(serviceConfig);
    }

    public void setExtensions(final Collection<Class<? extends Extension>> classes) {
        for (Class<? extends Extension> clazz : classes) {
            addExtension(clazz);
        }
    }


    public void setEnabledAlternativeStereotypeMetadatas(final Collection<Metadata<String>> enabledAlternativeStereotypesP) {
        this.enabledAlternativeStereotypes = new ArrayList<>();
        for (Metadata<String> s : enabledAlternativeStereotypesP) {
            this.enabledAlternativeStereotypes.add(s);
        }
    }

    public void setEnabledAlternativeStereotypes(Collection<Class<?>> classes) {
        this.enabledAlternativeStereotypes = new ArrayList<>();
        for (Class<?> clazz : classes) {
            addEnabledAlternativeStereotype(clazz);
        }
    }

    public void setExtensionMetadata(final Collection<Metadata<? extends Extension>> extensions) {
        for (Metadata<? extends Extension> e : extensions) {
            this.extensions.add((Metadata<Extension>) e);
        }
    }
}
