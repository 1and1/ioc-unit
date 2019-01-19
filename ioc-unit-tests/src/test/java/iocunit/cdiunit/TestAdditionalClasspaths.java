package iocunit.cdiunit;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunitejb.cdiunit.ExternalInterface;
import com.oneandone.iocunitejb.resources.Resources;

@SutClasspaths({ ExternalInterface.class })
@ExcludedClasses(Resources.class)
@RunWith(IocUnitRunner.class)
public class TestAdditionalClasspaths {

    @Inject
    private ExternalInterface external;

    @Test
    public void testResolvedExternal() {
        Assert.assertNotNull(external);
    }


}
