package com.oneandone.iocunit.analyzer.test1;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class Test1A implements Test1Interface {
    @Override
    public String call() {
        return "Test1A";
    }
}
