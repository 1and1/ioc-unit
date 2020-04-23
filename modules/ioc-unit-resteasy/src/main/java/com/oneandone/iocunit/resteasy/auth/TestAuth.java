package com.oneandone.iocunit.resteasy.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(value=RUNTIME)
@Target(value={TYPE,METHOD})
public @interface TestAuth {
    String user() default "IocUnitUser";

    String[] value();
}
