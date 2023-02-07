package com.oneandone.cdi;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.AddOpens;

/**
 * @author aschoerk
 */
public class AddOpensTest {
    @Test
    public void testAddopens() {
        AddOpens.open("java.base", "java.lang", "java.util");
    }
}
