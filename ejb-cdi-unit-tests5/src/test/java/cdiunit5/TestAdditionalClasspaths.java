package cdiunit5;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jglue.cdiunit.AdditionalClasspaths;
import org.jglue.cdiunit.external.ExternalInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

@AdditionalClasspaths(ExternalInterface.class)
@ExtendWith(JUnit5Extension.class)
public class TestAdditionalClasspaths {

    @Inject
    private ExternalInterface external;

    @Test
    public void testResolvedExternal() {
        Assertions.assertNotNull(external);
    }


}
