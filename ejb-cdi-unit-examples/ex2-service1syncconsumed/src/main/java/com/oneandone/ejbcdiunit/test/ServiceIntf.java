package com.oneandone.ejbcdiunit.test;

/**
 * Created by aschoerk on 28.06.17.
 */
public interface ServiceIntf {

    long newRemoteEntity1(int intValue, String stringValue);

    String getRemoteStringValueFor(long id);

}
