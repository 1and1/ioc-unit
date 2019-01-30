package com.oneandone.iocunit.analyzer.rawtype;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
public class ParameterizedProducer {
    @Produces
    List<String> listProducer() {
        return new ArrayList<String>();
    }

}
