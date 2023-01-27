package iocunit.cdiunit;

import jakarta.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.filter.Filter;

@RunWith(IocUnitRunner.class)
@TestClasses(ThresholdFilter.class)
// prevent EvaluatorFilter from being found
@AnalyzerFlags(allowGuessing = false)
public class TestNonCDIClasses {

    @Inject
    private Filter foo;

    private ThresholdFilter bar;

    @Test(expected = StarterDeploymentException.class)
    public void testNonCDIClassDiscovery() {
        if (WeldSetupClass.isWeld1())
            throw new StarterDeploymentException(new RuntimeException());
    }
}
