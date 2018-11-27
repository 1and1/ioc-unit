package ejbcdiunit2.first.cdiunit;

import javax.inject.Inject;

import org.jglue.cdiunit.external.ExternalInterface;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.SutClasspaths;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

@SutClasspaths({ ExternalInterface.class })
@RunWith(EjbCdiUnit2Runner.class)
public class TestAdditionalClasspaths {

    @Inject
    private ExternalInterface external;

    @Test
    public void testResolvedExternal() {
        Assert.assertNotNull(external);
    }


}
