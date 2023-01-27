package cdiunit5;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunitejb.cdiunit.ExternalInterface;
import com.oneandone.iocunitejb.resources.Resources;

@SutClasspaths(ExternalInterface.class)
@ExcludedClasses(Resources.class)
@ExtendWith(IocJUnit5Extension.class)
public class TestAdditionalClasspaths {

    @Inject
    private ExternalInterface external;

    @Test
    public void testResolvedExternal() {
        Assertions.assertNotNull(external);
    }


}
