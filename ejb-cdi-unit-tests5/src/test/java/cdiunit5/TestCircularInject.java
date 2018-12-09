package cdiunit5;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.exceptions.DeploymentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.ExpectedStartupException;
import com.oneandone.cdi.tester.JUnit5Extension;

@ExtendWith(JUnit5Extension.class)
@TestClasses(CircularA.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test
    @ExpectedStartupException(DeploymentException.class)
    public void testCircularDependency() {
        circularA.get();
    }
}
