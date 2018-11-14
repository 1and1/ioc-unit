package com.oneandone.cdi.weldstarter;

import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.Metadata;

import javax.enterprise.inject.spi.DeploymentException;
import javax.enterprise.inject.spi.Extension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    protected void addAlternativeClass(Class clazz) {
        alternativeClasses.add(new Metadata<String>() {
            @Override
            public String getValue() {
                return clazz.getName();
            }

            @Override
            public String getLocation() {
                return "Alternative In Testcode";
            }
        });
    }

    protected void addEnabledAlternativeStereotype(Class clazz) {
        enabledAlternativeStereotypes.add(new Metadata<String>() {
            @Override
            public String getValue() {
                return clazz.getName();
            }

            @Override
            public String getLocation() {
                return "AlternativeStereotype In Testcode";
            }
        });
    }

    protected void addExtension(Class<? extends Extension> clazz) {
        this.extensions.add(new Metadata<Extension>() {
            @Override
            public Extension getValue() {
                try {
                    return (Extension) clazz.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String getLocation() {
                return "Alternative In Testcode";
            }
        });
    }

    public <S extends Service> void addService(ServiceConfig<S> serviceConfig) {
        services.add(serviceConfig);
    }

    public void setExtensions(final Collection<Class<? extends Extension>> classes) {
        for (Class<? extends Extension> clazz : classes) {
            addExtension(clazz);
        }
    }


}
