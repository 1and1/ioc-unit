package com.oneandone.iocunit.util;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class LoggerProducer {
    /**
     * Produces and returns a {@link Logger} for the given {@link InjectionPoint}.
     *
     * @param injectionPoint
     *            the InjectionPoint to produce the Logger for
     * @return the Logger
     */
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}
