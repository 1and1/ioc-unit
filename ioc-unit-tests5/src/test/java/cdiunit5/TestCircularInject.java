package cdiunit5;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.ExpectedStartupException;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;

@ExtendWith(JUnit5Extension.class)
@TestClasses(CircularA.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test
    @ExpectedStartupException(StarterDeploymentException.class)
    public void testCircularDependency() {
        circularA.get();
    }
}
