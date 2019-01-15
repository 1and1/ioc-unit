package com.oneandone.ejbcdiunit.excludedclasses;

import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRule;
import com.oneandone.cdi.weldstarter.StarterDeploymentException;
import com.oneandone.ejbcdiunit.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */
@TestClasses({ IndirectExcluding.class })
@SutClasses({ ToExclude.class })
public class IndirectExcludeByRuleTest {
    @Rule
    public IocUnitRule getEjbUnitRule() {
        return new IocUnitRule(this);
    }

    @Inject
    ToExclude toExclude;

    @Test(expected = StarterDeploymentException.class)
    public void test() {
        fail("test should not start");
    }
}
