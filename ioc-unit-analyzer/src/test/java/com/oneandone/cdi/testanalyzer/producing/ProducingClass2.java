package com.oneandone.cdi.testanalyzer.producing;

import com.oneandone.cdi.testanalyzer.test2.Test2B;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class ProducingClass2 {

    @Produces
    static Test2B test2BProducer() {
        return new Test2B();
    }
}
