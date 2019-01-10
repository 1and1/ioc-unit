package com.oneandone.cdi.testanalyzer.excludedclasses.pcktoinclude;

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
