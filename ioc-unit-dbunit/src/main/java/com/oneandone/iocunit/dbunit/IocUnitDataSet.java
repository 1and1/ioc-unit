package com.oneandone.iocunit.dbunit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aschoerk
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface IocUnitDataSet {
    String[] value() default {};

    boolean order() default true;

    String unitName() default "";
}
