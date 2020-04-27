package org.jboss.weld.resources.spi;

import java.util.concurrent.ScheduledExecutorService;

import org.jboss.weld.bootstrap.api.Service;

/**
 * @author aschoerk
 */
public interface ScheduledExecutorServiceFactory extends Service {
    ScheduledExecutorService get();
}
