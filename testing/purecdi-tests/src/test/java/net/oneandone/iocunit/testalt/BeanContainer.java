package net.oneandone.iocunit.testalt;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class BeanContainer {

    @Produces
    Bean bean = new Bean();
}
