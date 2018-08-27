package cdiunit5;

import javax.inject.Inject;

import org.jboss.weld.exceptions.DeploymentException;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.ExpectedStartupException;
import com.oneandone.ejbcdiunit5.JUnit5Extension;

import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.filter.Filter;

@ExtendWith(JUnit5Extension.class)
@AdditionalClasses(ThresholdFilter.class)
public class TestNonCDIClasses {

    @Inject
    private Filter foo;

    private ThresholdFilter bar;

    @Test
    @ExpectedStartupException(DeploymentException.class)
    public void testNonCDIClassDiscovery() {

    }
}
