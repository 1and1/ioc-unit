package com.oneandone.ejbcdiunit.excludedclasses;

import static org.hamcrest.Matchers.is;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.EjbUnitRule;
import com.oneandone.ejbcdiunit.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.ejbcdiunit.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */
@AdditionalPackages({ ToInclude.class })
public class ExcludeByRuleTest {

    @Inject
    ToInclude toInclude;
    @Produces
    ToExclude.ToExcludeProduced tmp = new ToExclude.ToExcludeProduced(11); // no produces clash with excluded ToExclude
    @Inject
    ToExclude.ToExcludeProduced toExcludeProduced;

    @Rule
    public EjbUnitRule getEjbUnitRule() {
        ToInclude.count = 0;
        return new EjbUnitRule(this, new CdiTestConfig().addExcluded(ToExclude.class));
    }

    @Test
    public void test() {
        Assert.assertThat(toInclude.count, is(1));
        Assert.assertThat(toExcludeProduced.getValue(), is(11));
    }
}
