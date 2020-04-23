package com.oneandone.iocunit.basetests.junit;

import org.junit.Before;

/**
 * @author aschoerk
 */
public class SuperTestClass {
    protected int i = 0;
    protected boolean hiddenBeforeNotHidden = false;
    @Before
    public void beforeInSuperClass() {
        i ++;
    }

    @Before
    public void beforeInSuperClass2() {
        i ++;
    }

    @Before
    public void before() {
        hiddenBeforeNotHidden = true;
    }




}
