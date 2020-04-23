package com.oneandone.iocunit.analyzer.rawtype;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class RawProducer {
    @Produces
    List listProducer() {
        return new ArrayList<String>();
    }
}
