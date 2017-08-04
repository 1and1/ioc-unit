package com.oneandone.ejbcdiunit.excludedclasses;

import static org.junit.Assert.fail;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ IndirectExcluding.class })
public class IndirectExcludeTest {

    @Test(expected = RuntimeException.class)
    public void test() {
        fail("test should not start");
    }
}
