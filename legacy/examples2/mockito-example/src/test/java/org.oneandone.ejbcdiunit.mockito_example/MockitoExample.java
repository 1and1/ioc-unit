package org.oneandone.ejbcdiunit.mockito_example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

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
        lenient().when(testClass.booleanMethod(eq(true))).thenReturn(1);
        lenient().when(testClass.booleanMethod(eq(false))).thenReturn(2);
    }

    @Test
    public void test() {
        assertEquals(1, testClass.booleanMethod(true));
        assertEquals(2, testClass.booleanMethod(false));
    }

    interface Poops {
        String get(boolean is);
    }

    @Test
    void test1() {
        Poops a = mock(Poops.class);

        lenient().when(a.get(eq(true))).thenReturn("1");
        lenient().when(a.get(eq(false))).thenReturn("2");

        assertEquals("1", a.get(true));
        assertEquals("2", a.get(false));
    }

}
