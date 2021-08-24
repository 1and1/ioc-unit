package com.oneandone.cdi.discoveryrunner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author aschoerk
 */
public class TestScheduledExecutorServiceFactory implements org.jboss.weld.resources.spi.ScheduledExecutorServiceFactory {

    @Override
    public ScheduledExecutorService get() {
        return new ScheduledThreadPoolExecutor(10);
    }

    @Override
    public void cleanup() {

    }
}
