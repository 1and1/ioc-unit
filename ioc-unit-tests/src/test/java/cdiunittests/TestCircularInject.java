package cdiunittests;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;

@RunWith(IocUnitRunner.class)
@SutClasses(CircularA.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test(expected = StarterDeploymentException.class)
    public void testCircularDependency() {
        circularA.get();
    }
}
