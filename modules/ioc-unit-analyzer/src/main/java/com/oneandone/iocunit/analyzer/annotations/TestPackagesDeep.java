package com.oneandone.iocunit.analyzer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies available Testclasses from the "system under test" which may be used to satisfy injection requirements when
 * CDI gets started. The classes are defined as contained as part of the packages and subpackages of the classes
 * provided in value. May clash with alternatives.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestPackagesDeep {
    /**
     * @return Array of classes to make discoverable during testing.
     */
    public Class<?>[] value();
    /**
     * A regular expression can be defined to filter the resulting classes by name.
     * @return the regular expression that should match the className or "" which matches all
     */
    public String filteringRegex() default "";
}
