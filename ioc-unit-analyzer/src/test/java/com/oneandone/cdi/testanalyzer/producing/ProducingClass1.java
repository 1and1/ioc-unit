package com.oneandone.cdi.testanalyzer.producing;

import com.oneandone.cdi.testanalyzer.test1.Qualifier1A;
import com.oneandone.cdi.testanalyzer.test1.Test1A;
import com.oneandone.cdi.testanalyzer.test1.Test1B;

import javax.enterprise.inject.Produces;

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
