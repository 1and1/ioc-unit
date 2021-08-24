package com.oneandone.cdi.discoveryrunner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies classes containing test code which must be instantiated when CDI gets started.
 * These classes and their producers have priority over classes provided by SutPackages and SutClasspaths.
 * So if ambiguity is to be avoided, classes from here are prefered.
 * <p>
 * May not clash with alternatives.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(TestClasses.All.class)
public @interface TestClasses {
    /**
     * @return Array of classes to make discoverable during testing.
     */
    public Class<?>[] value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface All {
        TestClasses[] value();
    }

}
