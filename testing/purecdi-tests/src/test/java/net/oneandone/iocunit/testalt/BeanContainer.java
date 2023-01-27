package net.oneandone.iocunit.testalt;

import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class BeanContainer {

    @Produces
    Bean bean = new Bean();
}
