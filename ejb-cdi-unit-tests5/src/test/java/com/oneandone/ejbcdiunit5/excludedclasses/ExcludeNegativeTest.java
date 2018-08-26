package com.oneandone.ejbcdiunit5.excludedclasses;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToInclude;
import org.hamcrest.MatcherAssert;
import org.jglue.cdiunit.AdditionalPackages;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalPackages({ ToInclude.class })
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
