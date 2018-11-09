package com.oneandone.ejbcdiunit.weldstarter;

import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.spi.DeploymentException;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.spi.Metadata;

public interface WeldSetup {


    Collection<String> getBeanClasses();

    List<Metadata<String>> getAlternativeClasses();

    List<Metadata<String>> getEnabledAlternativeStereotypes();

    void setDeploymentException(DeploymentException deploymentException);

    Iterable<Metadata<Extension>> getExtensions();
}
