package com.oneandone.iocunit.analyzer.producing;

import com.oneandone.iocunit.analyzer.test1.Qualifier1A;
import com.oneandone.iocunit.analyzer.test1.Test1A;
import com.oneandone.iocunit.analyzer.test1.Test1B;

import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class ProducingClass1 {

    @Produces
    Test1A test1AProducer() {
        return new Test1A();
    }

    @Produces
    @Qualifier1A
    Test1B test1BProducer() {
        return new Test1B();
    }

}
