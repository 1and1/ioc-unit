package weldjunit;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.jboss.weld.resources.spi.ScheduledExecutorServiceFactory;

/**
 * @author aschoerk
 */
public class WJScheduledExecutorServiceFactory implements ScheduledExecutorServiceFactory {
    @Override
    public ScheduledExecutorService get() {
        return new ScheduledThreadPoolExecutor(10);
    }

    @Override
    public void cleanup() {

    }
}
