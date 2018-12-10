package ejbcdiunit2.first.cdiunit;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;

import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.filter.Filter;

@RunWith(CdiUnit2Runner.class)
@TestClasses(ThresholdFilter.class)
public class TestNonCDIClasses {

    @Inject
    private Filter foo;

    private ThresholdFilter bar;

    @Test(expected = StarterDeploymentException.class)
    public void testNonCDIClassDiscovery() {

    }
}
