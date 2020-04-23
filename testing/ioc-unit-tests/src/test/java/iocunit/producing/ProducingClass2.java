package iocunit.producing;

import javax.enterprise.inject.Produces;

import iocunit.test2.Test2B;

/**
 * @author aschoerk
 */
public class ProducingClass2 {

    @Produces
    static Test2B test2BProducer() {
        return new Test2B();
    }
}
