package com.oneandone.iocunitejb.example1_el;

/**
 * Created by aschoerk on 28.06.17.
 */
public interface ServiceIntf {

    int returnFive();

    long newEntity1(int intValue, String stringValue);

    String getStringValueFor(long id);


}
