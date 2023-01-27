package com.oneandone.iocunit.basetests.rawtype;

import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class RawListSubProducer {
    @Produces
    RawListSub produce() {
        return new RawListSub();
    }
}
