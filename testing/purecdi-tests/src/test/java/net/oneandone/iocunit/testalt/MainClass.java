package net.oneandone.iocunit.testalt;

import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
public class MainClass {
    @Inject
    Bean bean;
    @Inject
    Integer producedInt;
}
