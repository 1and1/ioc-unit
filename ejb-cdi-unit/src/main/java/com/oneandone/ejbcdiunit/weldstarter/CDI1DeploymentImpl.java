package com.oneandone.ejbcdiunit.weldstarter;

import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;

/**
 * @author aschoerk
 */
public class CDI1DeploymentImpl implements Deployment {
    private final BeanDeploymentArchive oneDeploymentArchive;
    private final ServiceRegistry services;
    private final Iterable<Metadata<Extension>> extensions;

    public CDI1DeploymentImpl(final ServiceRegistry services, final BeanDeploymentArchive oneDeploymentArchive,
            Iterable<Metadata<Extension>> extensions) {
        this.services = services;
        this.oneDeploymentArchive = oneDeploymentArchive;
        this.extensions = extensions;
    }

    @Override
    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
        return Arrays.asList(oneDeploymentArchive);
    }

    @Override
    public BeanDeploymentArchive loadBeanDeploymentArchive(final Class<?> beanClass) {
        return oneDeploymentArchive;
    }

    @Override
    public ServiceRegistry getServices() {
        return oneDeploymentArchive.getServices();
    }

    @Override
    public Iterable<Metadata<Extension>> getExtensions() {
        return extensions;
    }
}
