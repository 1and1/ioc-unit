package org.oneandone.ejbcdiunit.mockito_example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author aschoerk
 */
@ExtendWith(MockitoExtension.class)
public class MockitoExample {

    static abstract class TestClass {
        public abstract int booleanMethod(boolean arg);
    }

    @Mock
    TestClass testClass;

    @BeforeEach
    public void beforeEach() {
        when(testClass.booleanMethod(eq(true))).thenReturn(1);
        when(testClass.booleanMethod(eq(false))).thenReturn(2);
    }

    @Test
    public void test() {
        when(testClass.booleanMethod(eq(true))).thenReturn(1);
        when(testClass.booleanMethod(eq(false))).thenReturn(2);
        assertEquals(1, testClass.booleanMethod(true));
        assertEquals(2, testClass.booleanMethod(false));
    }
}
