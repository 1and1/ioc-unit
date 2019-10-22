package com.oneandone.iocunit.ejb.trainterceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * Used to annotate classes which should be intercepted by {@link TransactionalInterceptorBase}
 * @author aschoerk
 */
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EjbTransactional {
}
