package org.oneandone.ejbcdiunit.mockito_example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willReturn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author aschoerk
 */
@ExtendWith(MockitoExtension.class)
public class MockitoIntBDDExample {

    static abstract class TestClass {
        public abstract int intMethod(int arg);
    }

    @Mock
    TestClass testClass;

    @BeforeEach
    public void beforeEach() {
        willReturn(1).given(testClass.intMethod(1)).intValue();
        willReturn(2).given(testClass.intMethod(2)).intValue();
    }

    @Test
    public void test() {
        assertEquals(1, testClass.intMethod(1));
        assertEquals(2, testClass.intMethod(2));
    }
}
