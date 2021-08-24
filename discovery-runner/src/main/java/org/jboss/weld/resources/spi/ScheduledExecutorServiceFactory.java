//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.jboss.weld.resources.spi;

import java.util.concurrent.ScheduledExecutorService;

import org.jboss.weld.bootstrap.api.Service;

/**
 * copied from Weld to allow common use in Weld 2.3.5, 3.0 and 3.1
 */
public interface ScheduledExecutorServiceFactory extends Service {
    ScheduledExecutorService get();
}
