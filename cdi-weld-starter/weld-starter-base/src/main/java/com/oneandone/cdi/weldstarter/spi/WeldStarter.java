package com.oneandone.cdi.weldstarter.spi;

import com.oneandone.cdi.weldstarter.WeldSetup;

/**
 * @author aschoerk
 */
public interface WeldStarter {
    void start(WeldSetup weldSetup);

    <T> T get(Class<T> clazz);

    void tearDown();
}
