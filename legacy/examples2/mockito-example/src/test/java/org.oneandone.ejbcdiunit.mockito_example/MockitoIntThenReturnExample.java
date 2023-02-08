package org.oneandone.ejbcdiunit.mockito_example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * @author aschoerk
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MockitoIntThenReturnExample {

    static abstract class TestClass {
        public abstract int intMethod(int arg);
    }

    @Mock
    TestClass testClass;

    @BeforeEach
    public void beforeEach() {
        when(testClass.intMethod(eq(1))).thenReturn(1);
        when(testClass.intMethod(eq(2))).thenReturn(2);
    }

    @Test
    public void test() {
        assertEquals(1, testClass.intMethod(1));
        assertEquals(2, testClass.intMethod(2));
    }
}
