package com.oneandone.iocunit.analyzer.producing;

import com.oneandone.iocunit.analyzer.test2.Test2B;

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
