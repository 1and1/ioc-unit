package com.oneandone.iocunit.resteasytester;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class InjectTest {
    public void callInjectTest() {
        System.out.println("called InjectTest");
    }
}
