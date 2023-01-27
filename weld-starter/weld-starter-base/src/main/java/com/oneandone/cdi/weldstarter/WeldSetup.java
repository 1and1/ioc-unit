package com.oneandone.cdi.weldstarter;

import java.util.Collection;
import java.util.List;

import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.Metadata;

/**
 * Common interface of objects used to start weld by all weld-starters.
 */
public interface WeldSetup {
    class ServiceConfig<S extends Service> {
        public ServiceConfig(Class<S> serviceClass, S service) {
            this.serviceClass = serviceClass;
            this.service = service;
        }

        public Class<S> serviceClass;
        public S service;
    }

    Integer getNewInstanceNumber();

    Collection<String> getBeanClasses();

    List<Metadata<String>> getAlternativeClasses();

    List<Metadata<String>> getEnabledAlternativeStereotypes();

    List<Metadata<String>> getEnabledDecorators();

    List<ServiceConfig> getServices();

    List<Metadata<String>> getEnabledInterceptors();

    void setDeploymentException(DeploymentException deploymentException);

    Iterable<Metadata<Extension>> getExtensions();

    void registerServices(ServiceRegistry serviceRegistry);

}
