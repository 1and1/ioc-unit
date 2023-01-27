package net.oneandone.iocunit.testalt;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
@Alternative
public class BeanContainerAlt extends BeanContainer {

    @Produces
    int producerInt = 10;
}
