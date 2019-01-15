package com.oneandone.ejbcdiunit;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.iocunit.analyzer.InitialConfiguration;
import com.oneandone.cdi.tester.CdiUnit2Rule;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.ejbcdiunit.ejbs.SingletonTimerEJB;

/**
 * Test if initializations of EjbUnitRunner work.
 *
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class SimpleTest {

    @Rule
    public CdiUnit2Rule createEjbUnitRule() {
        return new CdiUnit2Rule(this, new InitialConfiguration().exclude(SingletonTimerEJB.class));
    }

    @Before
    public void beforeSimpleTest() {

    }

    @Test
    public void test() {
        TestBooleanClass t = new TestBooleanClass();
        assertFalse(t.b);
        assert true;
    }

    static class TestBooleanClass {
        boolean b;
    }

    @RunWith(CdiUnit2Runner.class)
    class Member {
        @Test
        public void test2() {
            assert false;
        }
    }


}
