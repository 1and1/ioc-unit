package ejbcdiunit2.first.producing;

import javax.enterprise.inject.Produces;

import ejbcdiunit2.first.test2.Test2B;

/**
 * @author aschoerk
 */
public class ProducingClass2 {

    @Produces
    static Test2B test2BProducer() {
        return new Test2B();
    }
}
