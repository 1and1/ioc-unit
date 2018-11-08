package com.oneandone.ejbcdiunit.closure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SutPackages {
    /**
     * @return Array of classes to make discoverable during testing.
     */
    public Class<?>[] value();

}
