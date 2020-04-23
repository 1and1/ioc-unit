package net.oneandone.ejbcdiunit.purecdi.rawtype;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class StringListProducer {
    @Produces
    StringList listProducer() {
        return new StringList();
    }

}
