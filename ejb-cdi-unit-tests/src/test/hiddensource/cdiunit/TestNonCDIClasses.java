package cdiunit;

import javax.inject.Inject;

import org.jboss.weld.exceptions.DeploymentException;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.filter.Filter;

@RunWith(EjbUnitRunner.class)
@AdditionalClasses(ThresholdFilter.class)
public class TestNonCDIClasses {

    @Inject
    private Filter foo;

    private ThresholdFilter bar;

    @Test(expected = DeploymentException.class)
    public void testNonCDIClassDiscovery() {

    }
}
