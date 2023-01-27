package com.oneandone.iocunitejb.excludedclasses;

import static org.hamcrest.Matchers.is;

import jakarta.inject.Inject;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.iocunit.analyzer.InitialConfiguration;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocUnitRule;
import com.oneandone.iocunitejb.ejbs.SingletonTimerEJB;
import com.oneandone.iocunitejb.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.iocunitejb.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */
@SutPackages({ ToInclude.class })
public class ExcludeNegativeByRuleTest {

    @Inject
    ToInclude toInclude;
    @Inject
    ToExclude.ToExcludeProduced toExcludeProduced; // produced by not excluded ToExclude

    @Rule
    public IocUnitRule getEjbUnitRule() {
        ToInclude.count = 0;
        return new IocUnitRule(this, new InitialConfiguration().exclude(SingletonTimerEJB.class));
    }

    @Test
    public void test() {
        Assert.assertThat(toInclude.count, is(2));
        Assert.assertThat(toExcludeProduced.getValue(), is(10));
    }
}
