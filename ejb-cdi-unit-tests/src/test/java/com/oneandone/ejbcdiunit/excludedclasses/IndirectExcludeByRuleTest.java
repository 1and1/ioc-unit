package com.oneandone.ejbcdiunit.excludedclasses;

import static org.junit.Assert.fail;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.ejbcdiunit.EjbUnitRule;

/**
 * @author aschoerk
 */
@AdditionalClasses({ IndirectExcluding.class })
public class IndirectExcludeByRuleTest {
    @Rule
    public EjbUnitRule getEjbUnitRule() {
        return new EjbUnitRule(this);
    }

    @Test(expected = RuntimeException.class)
    public void test() {
        fail("test should not start");
    }
}
