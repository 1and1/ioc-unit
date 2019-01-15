package com.oneandone.iocunit.analyzer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies classes containing test code which must be instantiated when CDI gets started.
 * These classes and their producers have priority over classes provided by SutPackages and SutClasspaths.
 * So if ambiguity is to be avoided, classes from here are prefered.
 *
 * May not clash with alternatives.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestClasses {
    /**
     * @return Array of classes to make discoverable during testing.
     */
    public Class<?>[] value();
}
