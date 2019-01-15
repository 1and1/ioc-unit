package com.oneandone.iocunit.analyzer.test1;

import com.oneandone.iocunit.analyzer.test2.Test2Interface;

import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class ConnectingClass1 {
    private final Test2Interface test2;

    public Test2Interface getTest2() {
        return test2;
    }

    @Inject
    ConnectingClass1(Test2Interface test2) {
        this.test2 = test2;
    }
}
