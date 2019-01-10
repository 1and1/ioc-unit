package com.oneandone.cdi.testanalyzer.excludedclasses;

import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;
import com.oneandone.cdi.testanalyzer.annotations.SutPackages;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToExclude;
import com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude.ToInclude;
import org.junit.BeforeClass;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author aschoerk
 */
public abstract class AbstractExcludeTestHolder {

    @BeforeClass
    public static void initToInclude() {
        ToInclude.count = 0;
    }

    @SutPackages({ ToInclude.class })
    @ExcludedClasses({ ToExclude.class })
    static abstract class AbstractExcludeTest {
        @Inject
        ToInclude toInclude;

        @Produces  // not effective in abstract class
        ToExclude.ToExcludeProduced tmp = new ToExclude.ToExcludeProduced(11); // no produces clash with excluded ToExclude

        @Inject
        ToExclude.ToExcludeProduced toExcludeProduced;

    }

}
