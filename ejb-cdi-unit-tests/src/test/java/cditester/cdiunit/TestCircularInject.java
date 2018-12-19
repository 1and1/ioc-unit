package cditester.cdiunit;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;

@RunWith(CdiUnit2Runner.class)
@TestClasses(CircularA.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test(expected = StarterDeploymentException.class)
    public void testCircularDependency() {
        circularA.get();
    }
}
