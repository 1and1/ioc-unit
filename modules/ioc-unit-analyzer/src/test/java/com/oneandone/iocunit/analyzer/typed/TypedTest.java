package com.oneandone.iocunit.analyzer.typed;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.BaseTest;

/**
 * @author aschoerk
 */
public class TypedTest extends BaseTest {

    @Test
    public void test() {
        createTest(UsingClassTyped.class);
        assertTrue(toBeStarted.contains(TypedBaseClass.class));
        assertTrue(toBeStarted.contains(TypedSubClassTyped.class));
    }
}
