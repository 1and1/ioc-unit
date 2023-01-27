package com.oneandone.iocunitejb.excludedclasses;

import static org.hamcrest.Matchers.is;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.iocunit.analyzer.InitialConfiguration;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.IocUnitRule;
import com.oneandone.iocunitejb.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.iocunitejb.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */
@TestPackages({ ToInclude.class })
public class ExcludeByRuleTest {

    @Inject
    ToInclude toInclude;
    @Produces
    ToExclude.ToExcludeProduced tmp = new ToExclude.ToExcludeProduced(11); // no produces clash with excluded ToExclude
    @Inject
    ToExclude.ToExcludeProduced toExcludeProduced;

    @Rule
    public IocUnitRule getEjbUnitRule() {
        ToInclude.count = 0;
        return new IocUnitRule(this, new InitialConfiguration().exclude(ToExclude.class));
    }

    @Test
    public void test() {
        Assert.assertThat(toInclude.count, is(1));
        Assert.assertThat(toExcludeProduced.getValue(), is(11));
    }
}
