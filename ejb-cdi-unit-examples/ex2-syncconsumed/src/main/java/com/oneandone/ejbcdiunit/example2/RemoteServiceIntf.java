package com.oneandone.ejbcdiunit.example2;

/**
 * @author aschoerk
 */
public interface RemoteServiceIntf {
    int returnFive();

    long newEntity1(int intValue, String stringValue);

    String getStringValueFor(long id);
}
