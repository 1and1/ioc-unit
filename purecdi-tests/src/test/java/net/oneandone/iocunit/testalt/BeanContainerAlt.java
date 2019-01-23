package net.oneandone.iocunit.testalt;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
@Alternative
public class BeanContainerAlt extends BeanContainer {

    @Produces
    int producerInt = 10;
}
