package com.oneandone.ejbcdiunit.excludedclasses;

import static org.hamcrest.Matchers.is;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.ejbcdiunit.EjbUnitRule;
import com.oneandone.ejbcdiunit.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.ejbcdiunit.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */
@AdditionalPackages({ ToInclude.class })
public class ExcludeNegativeByRuleTest {

    @Inject
    ToInclude toInclude;
    @Inject
    ToExclude.ToExcludeProduced toExcludeProduced; // produced by not excluded ToExclude

    @Rule
    public EjbUnitRule getEjbUnitRule() {
        ToInclude.count = 0;
        return new EjbUnitRule(this);
    }

    @Test
    public void test() {
        Assert.assertThat(toInclude.count, is(2));
        Assert.assertThat(toExcludeProduced.getValue(), is(10));
    }
}
