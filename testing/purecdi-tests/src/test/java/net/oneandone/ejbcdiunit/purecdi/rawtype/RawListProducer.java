package net.oneandone.ejbcdiunit.purecdi.rawtype;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class RawListProducer {
    @Produces
    List listProducer() {
        return new ArrayList<String>();
    }
}
