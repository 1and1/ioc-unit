package ejbcdiunit2.first.cdiunit;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.exceptions.DeploymentException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

@RunWith(EjbCdiUnit2Runner.class)
@TestClasses(CircularA.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test(expected = DeploymentException.class)
    public void testCircularDependency() {
        circularA.get();
    }
}
