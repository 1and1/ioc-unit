package com.oneandone.iocunit.basetests.rawtype;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class RawProducer {
    @Produces
    List listProducer() {
        return new ArrayList<String>();
    }
}
