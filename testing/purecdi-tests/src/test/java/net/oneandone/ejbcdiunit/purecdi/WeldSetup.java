package net.oneandone.ejbcdiunit.purecdi;

import org.jboss.weld.bootstrap.spi.Metadata;

import javax.enterprise.inject.spi.DeploymentException;
import java.util.Collection;
import java.util.List;

public interface WeldSetup {


    Collection<String> getBeanClasses();

    List<Metadata<String>> getAlternativeClasses();

    List<Metadata<String>> getEnabledAlternativeStereotypes();

    void setDeploymentException(DeploymentException deploymentException);

}
