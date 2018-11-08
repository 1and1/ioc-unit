package com.oneandone.ejbcdiunit.weldstarter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.CDI11Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;

/**
 * @author aschoerk
 */
public class Post1Deployment implements CDI11Deployment {

    private final BeanDeploymentArchive oneDeploymentArchive;
    private final ServiceRegistry services;

    public Post1Deployment(final ServiceRegistry services, final BeanDeploymentArchive oneDeploymentArchive) {
        this.services = services;
        this.oneDeploymentArchive = oneDeploymentArchive;
    }

    @Override
    public BeanDeploymentArchive getBeanDeploymentArchive(final Class<?> beanClass) {
        return oneDeploymentArchive;
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
        return Collections.emptyList();
    }
}
