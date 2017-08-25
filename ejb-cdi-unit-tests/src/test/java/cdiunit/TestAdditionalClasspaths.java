package cdiunit;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasspaths;
import org.jglue.cdiunit.external.ExternalInterface;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

@AdditionalClasspaths(ExternalInterface.class)
@RunWith(EjbUnitRunner.class)
public class TestAdditionalClasspaths {

    @Inject
    private ExternalInterface external;

    @Test
    public void testResolvedExternal() {
        Assert.assertNotNull(external);
    }


}
