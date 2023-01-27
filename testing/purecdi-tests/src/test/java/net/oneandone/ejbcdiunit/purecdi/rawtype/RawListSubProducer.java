package net.oneandone.ejbcdiunit.purecdi.rawtype;

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
