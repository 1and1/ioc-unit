package com.oneandone.iocunitejb.jms1_amqp_rabbitmq;

/**
 * @author aschoerk
 */
public interface RemoteServiceIntf {
    int returnFive();

    long newEntity1(int intValue, String stringValue);

    String getStringValueFor(long id);
}
