package com.oneandone.iocunit.analyzer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies classes from the "system under test" which must be instantiated when CDI gets started.
 * May not clash with alternatives.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SutClasses {
    /**
     * @return Array of classes to make discoverable during testing.
     */
    public Class<?>[] value();

    /**
     * @return Array of class names to make discoverable during testing (late binding allows specifying classes that are package visible).
     */
    public String[] late() default {};
}
