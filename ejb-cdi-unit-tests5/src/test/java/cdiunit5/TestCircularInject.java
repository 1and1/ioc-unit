package cdiunit5;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.exceptions.DeploymentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.ExpectedStartupException;
import com.oneandone.ejbcdiunit5.JUnit5Extension;

@ExtendWith(JUnit5Extension.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test
    @ExpectedStartupException(DeploymentException.class)
    public void testCircularDependency() {
        circularA.get();
    }
}
