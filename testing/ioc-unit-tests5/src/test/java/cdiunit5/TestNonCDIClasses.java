package cdiunit5;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.ExpectedStartupException;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;

import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.filter.Filter;

@ExtendWith(IocJUnit5Extension.class)
@SutClasses(ThresholdFilter.class)
public class TestNonCDIClasses {

    @Inject
    private Filter foo;

    private ThresholdFilter bar;

    @Test
    @ExpectedStartupException(StarterDeploymentException.class)
    public void testNonCDIClassDiscovery() {

    }
}
