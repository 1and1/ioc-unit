package com.oneandone.cdi.weldstarter;

/**
 * @author aschoerk
 */
public interface WeldStarter {
    void start(WeldSetup weldSetup);

    <T> T get(Class<T> clazz);

    void tearDown();
}
