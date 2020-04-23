package com.oneandone.iocunitejb.excludedclasses;

import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.iocunitejb.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ IndirectExcluding.class, ToExclude.class })
public class IndirectExcludeTest {

    @Inject
    ToExclude toExclude;

    @Test(expected = StarterDeploymentException.class)
    public void test() {
        fail("test should not start");
    }
}
