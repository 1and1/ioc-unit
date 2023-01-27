package com.oneandone.iocunit.analyzer.rawtype.producers;

import jakarta.enterprise.inject.Produces;

import com.oneandone.iocunit.analyzer.rawtype.types.RawListSub;

/**
 * @author aschoerk
 */
public class RawListSubProducer {
    @Produces
    RawListSub produce() {
        return new RawListSub();
    }
}
