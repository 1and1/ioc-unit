package com.oneandone.ejbcdiunit.test;

/**
 * @author aschoerk
 */
public interface RemoteServiceIntf {
    int returnFive();

    long newEntity1(int intValue, String stringValue);

    String getStringValueFor(long id);
}