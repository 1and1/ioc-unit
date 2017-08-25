package net.oneandone.example;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
