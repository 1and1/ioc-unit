package cditester.producing;

import javax.enterprise.inject.Produces;

import cditester.test1.Qualifier1A;
import cditester.test1.Test1A;
import cditester.test1.Test1B;

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
