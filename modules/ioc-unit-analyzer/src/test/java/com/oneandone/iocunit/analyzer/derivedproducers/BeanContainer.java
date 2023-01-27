package com.oneandone.iocunit.analyzer.derivedproducers;

import jakarta.enterprise.inject.Produces;

import org.mockito.Mock;

/**
 * @author aschoerk
 */
public class BeanContainer {

    @Mock
    @Produces
    Bean b = new Bean();
}
