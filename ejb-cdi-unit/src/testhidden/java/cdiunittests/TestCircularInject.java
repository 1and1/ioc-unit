package cdiunittests;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

@RunWith(EjbUnitRunner.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test(expected = StarterDeploymentException.class)
    public void testCircularDependency() {
        circularA.get();
    }
}
