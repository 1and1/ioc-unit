package com.oneandone.ejbcdiunit;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test if initializations of EjbUnitRunner work.
 *
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
public class SimpleTest {

    @Before
    public void beforeSimpleTest() {

    }

    static class TestBooleanClass {
        boolean b;
    }

    @Test
    public void test() {
        TestBooleanClass t = new TestBooleanClass();
        assertFalse (t.b);
        assert true;
    }

    @RunWith(EjbUnitRunner.class)
    class Member {
        @Test
        public void test2() {
            assert false;
        }
    }


}
