package cdiunit5;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.tester.ExpectedStartupException;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;

import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.filter.Filter;

@ExtendWith(JUnit5Extension.class)
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
