package com.oneandone.ejbcdiunit.excludedclasses;

import static org.junit.Assert.fail;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Rule;
import org.junit.Test;

import com.oneandone.cdi.tester.CdiUnit2Rule;

/**
 * @author aschoerk
 */
@AdditionalClasses({ IndirectExcluding.class })
public class IndirectExcludeByRuleTest {
    @Rule
    public CdiUnit2Rule getEjbUnitRule() {
        return new CdiUnit2Rule(this);
    }

    @Test(expected = RuntimeException.class)
    public void test() {
        fail("test should not start");
    }
}
