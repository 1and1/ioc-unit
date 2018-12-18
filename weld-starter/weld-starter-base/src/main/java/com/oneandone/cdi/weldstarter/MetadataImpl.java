package com.oneandone.cdi.weldstarter;

import org.jboss.weld.bootstrap.spi.Metadata;

/**
 * Helperclass to support creation Metadata necessary to start weld.
 *
 * @author aschoerk
 */
public class MetadataImpl<T> implements Metadata<T> {
    T value;
    String location = "default";

    public MetadataImpl(final T value) {
        this.value = value;
    }

    public MetadataImpl(final T value, final String location) {
        this.value = value;
        this.location = location;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getLocation() {
        return location;
    }
}
