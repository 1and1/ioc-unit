package com.oneandone.ejbcdiunit5.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class LoggerGenerator {
    @Produces
    Logger createLogger() {
        return LoggerFactory.getLogger("ioc-unit tests");
    }
}
