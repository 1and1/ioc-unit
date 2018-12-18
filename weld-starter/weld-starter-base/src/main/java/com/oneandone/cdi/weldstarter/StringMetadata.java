package com.oneandone.cdi.weldstarter;

import org.jboss.weld.bootstrap.spi.Metadata;

/**
 * Helper class to create String-Metadata before starting Weld.
 *
 * @author aschoerk
 */
public class StringMetadata implements Metadata<String> {
    private final String name;
    private String location = "default location";

    public StringMetadata(String name) {
        this.name = name;
    }

    public StringMetadata(Class clazz) {
        this(clazz.getName());
    }

    public StringMetadata(Class clazz, String location) {
        this(clazz.getName(), location);
    }


    public StringMetadata(String name, String location) {
        this(name);
        this.location = location;
    }

    @Override
    public String getValue() {
        return name;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "StringMetadata{" +
                "name='" + name + '\'' +
                '}';
    }
}
