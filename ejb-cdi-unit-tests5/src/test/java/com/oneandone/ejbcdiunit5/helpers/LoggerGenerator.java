package com.oneandone.ejbcdiunit5.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class LoggerGenerator {
    @Produces
    Logger createLogger() {
        return LoggerFactory.getLogger("ejb-cdi-unit tests");
    }
}
