package net.oneandone.iocunit.testalt;

import jakarta.annotation.PostConstruct;

/**
 * @author aschoerk
 */
public class Bean {
    boolean didPostConstruct = false;
    @PostConstruct
    public void postConstruct() {
        didPostConstruct = true;
    }

}
