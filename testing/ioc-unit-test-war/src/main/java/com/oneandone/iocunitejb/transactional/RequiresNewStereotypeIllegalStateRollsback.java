package com.oneandone.iocunitejb.transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Stereotype
@Transactional(
        value = Transactional.TxType.REQUIRES_NEW,
        dontRollbackOn = {RuntimeException.class},
        // IllegalStateException derived from RuntimeException therefore not rolling back.
        rollbackOn = {IllegalStateException.class,CheckedException.class})
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresNewStereotypeIllegalStateRollsback {
}
