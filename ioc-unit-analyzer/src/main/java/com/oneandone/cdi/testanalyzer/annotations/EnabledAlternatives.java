package com.oneandone.cdi.testanalyzer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to signify Alternatives to be enabled when CDI gets started. See beans.xml for more information, how to
 * provide the classes.
 *
 * <ul><li>alternative classes: themselves</li><li>alternative fields, methods: the declaring classes</li></ul>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnabledAlternatives {
    /**
     * @return Array of classes to make discoverable during testing.
     */
    public Class<?>[] value();
}
