package com.oneandone.iocunitejb.example4;

/**
 * @author aschoerk
 */
public interface RemoteServiceIntf {
    int returnFive();

    long newEntity1(int intValue, String stringValue);

    String getStringValueFor(long id);
}
