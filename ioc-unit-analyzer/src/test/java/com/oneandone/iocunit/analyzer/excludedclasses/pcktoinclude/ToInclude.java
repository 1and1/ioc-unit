package com.oneandone.iocunit.analyzer.excludedclasses.pcktoinclude;

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
