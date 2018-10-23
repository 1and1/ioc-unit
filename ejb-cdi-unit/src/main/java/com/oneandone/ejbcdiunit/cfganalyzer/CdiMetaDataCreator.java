package com.oneandone.ejbcdiunit.cfganalyzer;

import java.lang.reflect.Constructor;

import org.jboss.weld.bootstrap.spi.Metadata;

/**
 * @author aschoerk
 */
public class CdiMetaDataCreator {

    public static <T> Metadata<T> createMetadata(T value, String location) {
        try {
            return new org.jboss.weld.bootstrap.spi.helpers.MetadataImpl<>(value, location);
        } catch (NoClassDefFoundError e) {
            // MetadataImpl moved to a new package in Weld 2.4, old copy removed in 3.0
            try {
                // If Weld < 2.4, the new package isn't there, so we try the old package.
                // noinspection unchecked
                Class<Metadata<T>> oldClass = (Class<Metadata<T>>) Class.forName("org.jboss.weld.metadata.MetadataImpl");
                Constructor<Metadata<T>> ctor = oldClass.getConstructor(Object.class, String.class);
                return ctor.newInstance(value, location);
            } catch (ReflectiveOperationException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

}
