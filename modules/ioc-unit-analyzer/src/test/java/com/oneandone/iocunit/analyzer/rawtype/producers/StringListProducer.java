package com.oneandone.iocunit.analyzer.rawtype.producers;

import javax.enterprise.inject.Produces;

import com.oneandone.iocunit.analyzer.rawtype.types.StringList;

/**
 * @author aschoerk
 */
public class StringListProducer {
    @Produces
    StringList listProducer() {
        return new StringList();
    }

}
