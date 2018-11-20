package ejbcdiunit2.cdiunit.test2;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class ProducingClass2 {

    @Produces
    Test2A test1AProducer(Test2A test2A) {
        return test2A;
    }
}
