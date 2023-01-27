/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.oneandone.iocunit.contexts.internal;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.http.HttpSessionEvent;

/**
 * Enables us to inject initial listener. This interface has all the same methods as
 * WeldListener/WeldInitialListener, but allows us to avoid coding directly against
 * these interfaces (which moved around between Weld 1.x and Weld 3.x).
 * @author Sean Flanigan
 */
public interface InitialListener {
    void contextInitialized(ServletContextEvent sce) ;
    void contextDestroyed(ServletContextEvent sce);
    void sessionCreated(HttpSessionEvent event);
    void sessionDestroyed(HttpSessionEvent event);
    void requestDestroyed(ServletRequestEvent event);
    void requestInitialized(ServletRequestEvent event);
}
