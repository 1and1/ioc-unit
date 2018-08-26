package cdiunit5;

import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.filter.Filter;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jboss.weld.exceptions.DeploymentException;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

@ExtendWith(JUnit5Extension.class)
@AdditionalClasses(ThresholdFilter.class)
public class TestNonCDIClasses {

    @Inject
    private Filter foo;

    private ThresholdFilter bar;

    @Test
    public void testNonCDIClassDiscovery() {
        Assertions.assertThrows(DeploymentException.class, () -> {} );
    }
}
