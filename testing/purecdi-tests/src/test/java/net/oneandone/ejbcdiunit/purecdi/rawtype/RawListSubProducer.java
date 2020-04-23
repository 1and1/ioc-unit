package net.oneandone.ejbcdiunit.purecdi.rawtype;

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
