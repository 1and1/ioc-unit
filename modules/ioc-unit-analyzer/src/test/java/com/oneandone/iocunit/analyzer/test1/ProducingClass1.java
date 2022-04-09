package com.oneandone.iocunit.analyzer.test1;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class ProducingClass1 {

    @Produces
    Test1A test1AProducer() {
        return new Test1A();
    }
}
