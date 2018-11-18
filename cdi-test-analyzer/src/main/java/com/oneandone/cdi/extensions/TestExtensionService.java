package com.oneandone.cdi.extensions;

import java.util.Collection;

import javax.enterprise.inject.spi.Extension;

/**
 * @author aschoerk
 */
public interface TestExtensionService {
    Collection<Extension> getExtensions();
}
