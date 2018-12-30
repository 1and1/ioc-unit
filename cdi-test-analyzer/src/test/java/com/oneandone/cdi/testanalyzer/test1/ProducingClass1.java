package com.oneandone.cdi.testanalyzer.test1;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class ProducingClass1 {

    @Produces
    Test1A test1AProducer(Test1A test1A) {
        return test1A;
    }
}
