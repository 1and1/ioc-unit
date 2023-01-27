package cdiunit5;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ExpectedStartupException;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;

@ExtendWith(IocJUnit5Extension.class)
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
