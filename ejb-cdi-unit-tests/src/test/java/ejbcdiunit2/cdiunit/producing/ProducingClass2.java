package ejbcdiunit2.cdiunit.producing;

import javax.enterprise.inject.Produces;

import ejbcdiunit2.cdiunit.test2.Test2A;

/**
 * @author aschoerk
 */
public class ProducingClass2 {

    @Produces
    Test2A test1AProducer() {
        return new Test2A();
    }
}
