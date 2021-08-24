package com.oneandone.cdi.discoveryrunner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies classes to be explicitly be prevented from instantiation before the CDI-Container gets started.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ExcludedClasses.All.class)
public @interface ExcludedClasses {
    /**
     * @return Array of classes to make discoverable during testing.
     */
    public Class<?>[] value();

    /**
     * @return Array of class names to make discoverable during testing (late binding allows specifying classes that are package visible).
     */
    public String[] late() default {};


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface All {
        ExcludedClasses[] value();
    }

}
