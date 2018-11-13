package com.oneandone.cdi.testanalyzer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies classes to be explicitly be prevented from instantiation before the CDI-Container gets started.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcludedClasses {
    /**
     * @return Array of classes to make discoverable during testing.
     */
    public Class<?>[] value();

    /**
     * @return Array of class names to make discoverable during testing (late binding allows specifying classes that are package visible).
     */
    public String[] late() default {};
}
