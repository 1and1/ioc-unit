package com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude;

import jakarta.annotation.PostConstruct;

/**
 * @author aschoerk
 */
public class ToInclude {
    public static int count;

    @PostConstruct
    public void postConstruct() {
        count++;
    }
}
