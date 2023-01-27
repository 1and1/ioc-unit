package com.oneandone.ejbcdiunit5.excludedclasses;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ExpectedStartupException;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@TestClasses({ IndirectExcluding.class, ToExclude.class  })
public class IndirectExcludeTest {

    @Inject
    ToExclude toExclude;

    @Test
    @ExpectedStartupException(StarterDeploymentException.class)
    public void test() {
        throw new RuntimeException("should be hidden by StartupException");
    }
}
