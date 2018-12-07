package cdiunit;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.exceptions.DeploymentException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

@RunWith(EjbUnitRunner.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test(expected = DeploymentException.class)
    public void testCircularDependency() {
        circularA.get();
    }
}
