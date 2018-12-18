package cdiunit5;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.SutClasspaths;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.ejbcdiunit.cdiunit.ExternalInterface;

@SutClasspaths(ExternalInterface.class)
@ExtendWith(JUnit5Extension.class)
public class TestAdditionalClasspaths {

    @Inject
    private ExternalInterface external;

    @Test
    public void testResolvedExternal() {
        Assertions.assertNotNull(external);
    }


}
