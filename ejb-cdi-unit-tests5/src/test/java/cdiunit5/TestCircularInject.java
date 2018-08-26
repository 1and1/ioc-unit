package cdiunit5;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jboss.weld.exceptions.DeploymentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.inject.Provider;

@ExtendWith(JUnit5Extension.class)
public class TestCircularInject {
    @Inject
    private Provider<CircularA> circularA;

    @Test
    public void testCircularDependency() {
        Assertions.assertThrows(DeploymentException.class, () -> circularA.get());
    }
}
