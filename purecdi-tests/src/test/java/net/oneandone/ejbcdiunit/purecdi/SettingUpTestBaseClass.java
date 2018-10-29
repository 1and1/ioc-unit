package net.oneandone.ejbcdiunit.purecdi;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;

import javax.enterprise.inject.spi.DeploymentException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author aschoerk
 */
public abstract class SettingUpTestBaseClass implements WeldSetup {
    WeldStarter weldStarter = new WeldStarter();
    DeploymentException deploymentException;

    public Collection<String> getBeanClasses() {
        return Collections.EMPTY_LIST;
    }

    public List<Metadata<String>> getAlternativeClasses() {
        return Collections.EMPTY_LIST;
    }

    public List<Metadata<String>> getEnabledAlternativeStereotypes() {
        return Collections.EMPTY_LIST;
    }

    public void setDeploymentException(
            DeploymentException deploymentException) {
        this.deploymentException = deploymentException;
    }

    protected WeldContainer getContainer() {
        return weldStarter.container;
    }

    @Before
    public void setUp() {
        weldStarter.setUp(this);
    }


    @After
    public void tearDown() {
        weldStarter.tearDown();
    }
}
