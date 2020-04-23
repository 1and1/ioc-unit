package com.oneandone.iocunit.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aschoerk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ValidateClasses {
    /**
     * @return Array of classes to intercept by ValidationInterceptor
     */
    public Class<?>[] value();

}
