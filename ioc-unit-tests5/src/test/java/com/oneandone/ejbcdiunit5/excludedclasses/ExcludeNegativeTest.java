package com.oneandone.ejbcdiunit5.excludedclasses;

import static org.hamcrest.Matchers.is;

import javax.inject.Inject;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.JUnit5Extension;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToInclude;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@SutPackages({ ToInclude.class })
public class ExcludeNegativeTest {

    @Inject
    ToInclude toInclude;
    @Inject
    ToExclude.ToExcludeProduced toExcludeProduced; // produced by not excluded ToExclude

    @BeforeAll
    public static void initToInclude() {
        ToInclude.count = 0;
    }

    @Test
    public void test() {
        MatcherAssert.assertThat(toInclude.count, is(2));
        MatcherAssert.assertThat(toExcludeProduced.getValue(), is(10));
    }
}
