package com.oneandone.iocunit.basetests.rawtype;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class RawListSubProducer {
    @Produces
    RawListSub produce() {
        return new RawListSub();
    }
}
