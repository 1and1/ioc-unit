package com.oneandone.iocunit.analyzer.typed;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.iocunit.analyzer.BaseTest;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class TypedTest extends BaseTest {

    @Test
    public void test() {
        createTest(UsingClass.class);
        assertTrue(toBeStarted.contains(BaseClass.class));
        assertTrue(toBeStarted.contains(TypedSubClass.class));
    }
}
