package com.oneandone.cdi.discoveryrunner;


import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WeldDiscoveryExtension.class)
public class TestJUnit5Extension {
    @Inject
    Bean bean;

    @Test
    public void test() {
        assertEquals((Integer) 10, bean.returnInt(10));
    }

    @Test
    public void test2() {
        assertEquals((Integer) 10, bean.returnInt(10));
    }


}
