package com.oneandone.cdi.weldstarter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.Metadata;

/**
 * Describes a configuration, that can be used to start CDI using a weld-starter.
 */
public class WeldSetupBase implements WeldSetup {
    static AtomicInteger instanceNumber = new AtomicInteger();

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
    public Integer getNewInstanceNumber() {
        return instanceNumber.incrementAndGet();
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

    public void setEnabledInterceptors(List<Metadata<String>> classes) {
        enabledInterceptors = classes;
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
            addExtensionObject(clazz.newInstance());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void addExtensionObject(final Extension extension) {
        this.extensions.add(new MetadataImpl<Extension>(extension, "Alternative In Testcode"));
    }

    public void handleWeldExtensions(final Method method) {
        try {
            final Iterable<Metadata<Extension>> localExtensions = getExtensions();
            extensions = new ArrayList<>();
            for (Metadata<Extension> extensionMetadata : localExtensions) {
                final Class<? extends Extension> extensionClass = extensionMetadata.getValue().getClass();
                if (extensionClass.getName().contains(".ProducerConfigExtension")) {
                    Constructor<? extends Extension> constructor =
                            extensionClass.getConstructor(Method.class);
                    Extension producerConfig = constructor.newInstance(method);
                    addExtensionObject(producerConfig);
                } else {
                    final Constructor<?>[] declaredConstructors = extensionClass.getDeclaredConstructors();
                    if (declaredConstructors.length == 1 && declaredConstructors[0].getParameters().length == 0) {
                        addExtensionObject(extensionClass.newInstance());
                    } else {
                        addExtensionObject(extensionMetadata.getValue());
                    }
                }
            }
        } catch (Exception e1) {
            throw new RuntimeException(e1);
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
