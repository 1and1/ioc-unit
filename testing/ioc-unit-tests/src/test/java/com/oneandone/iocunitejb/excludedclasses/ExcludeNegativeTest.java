package com.oneandone.iocunitejb.excludedclasses;

import static org.hamcrest.Matchers.is;

import jakarta.inject.Inject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunitejb.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.iocunitejb.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutPackages({ ToInclude.class })
// @ExcludedClasses(ToExclude.ToExcludeProduced.class)
public class ExcludeNegativeTest {

    @Inject
    ToInclude toInclude;
    @Inject
    ToExclude.ToExcludeProduced toExcludeProduced; // produced by not excluded ToExclude

    @BeforeClass
    public static void initToInclude() {
        ToInclude.count = 0;
    }

    @Test
    public void test() {
        Assert.assertThat(toInclude.count, is(2));
        Assert.assertThat(toExcludeProduced.getValue(), is(10));
    }
}
