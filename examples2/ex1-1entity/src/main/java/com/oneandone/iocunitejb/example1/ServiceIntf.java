package com.oneandone.iocunitejb.example1;

import javax.validation.constraints.NotNull;


/**
 * Created by aschoerk on 28.06.17.
 */
public interface ServiceIntf {

    int returnFive();

    long newEntity1(int intValue, @NotNull String stringValue);

    String getStringValueFor(long id);

}
