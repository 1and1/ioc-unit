package com.oneandone.iocunit.analyzer.test2;

import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class ProducingClass2InTest2 {

    @Produces
    Test2A test2AProducer(Test2A test2A) {
        return test2A;
    }
}
