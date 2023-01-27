package com.oneandone.ejbcdiunit5.excludedclasses;

import static org.hamcrest.Matchers.is;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@SutPackages({ ToInclude.class })
@ExcludedClasses({ ToExclude.class })
public class ExcludeTest {

    @Inject
    ToInclude toInclude;
    @Produces
    ToExclude.ToExcludeProduced tmp = new ToExclude.ToExcludeProduced(11); // no produces clash with excluded ToExclude
    @Inject
    ToExclude.ToExcludeProduced toExcludeProduced;

    @BeforeAll
    public static void initToInclude() {
        ToInclude.count = 0;
    }

    @Test
    public void test() {
        MatcherAssert.assertThat(toInclude.count, is(1));
        MatcherAssert.assertThat(toExcludeProduced.getValue(), is(11));
    }
}
