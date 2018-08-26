package com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude;

import javax.annotation.PostConstruct;

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
