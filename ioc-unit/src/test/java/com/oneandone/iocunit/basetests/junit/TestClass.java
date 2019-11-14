package com.oneandone.iocunit.basetests.junit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
public class TestClass extends SuperTestClass {
    int i2 = 0;
    @Before
    public void before() {
        i2 = 1;
    }

    @Test
    public void doesCallBothBeforeMethods() {
        Assert.assertEquals(2, i);
        Assert.assertEquals(1, i2);
        Assert.assertFalse(hiddenBeforeNotHidden);
    }
}
