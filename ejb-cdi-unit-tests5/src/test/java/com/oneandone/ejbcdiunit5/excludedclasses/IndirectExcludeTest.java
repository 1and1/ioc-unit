package com.oneandone.ejbcdiunit5.excludedclasses;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.SutClasses;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.ExpectedStartupException;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
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
