package com.oneandone.iocunitejb.example1;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.internal.cdi.interceptor.MethodValidated;

/**
 * Created by aschoerk on 28.06.17.
 */
@MethodValidated
public interface ServiceIntf {

    int returnFive();

    long newEntity1(int intValue, @NotNull String stringValue);

    String getStringValueFor(long id);


}
