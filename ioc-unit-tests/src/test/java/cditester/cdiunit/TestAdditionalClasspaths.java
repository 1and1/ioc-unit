package cditester.cdiunit;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.CdiUnit2Runner;
import com.oneandone.ejbcdiunit.cdiunit.ExternalInterface;

@SutClasspaths({ ExternalInterface.class })
@RunWith(CdiUnit2Runner.class)
public class TestAdditionalClasspaths {

    @Inject
    private ExternalInterface external;

    @Test
    public void testResolvedExternal() {
        Assert.assertNotNull(external);
    }


}
