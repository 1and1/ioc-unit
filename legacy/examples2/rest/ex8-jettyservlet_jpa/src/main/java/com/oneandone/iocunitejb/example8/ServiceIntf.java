package com.oneandone.iocunitejb.example8;

import jakarta.ws.rs.core.Response;

/**
 * Created by aschoerk on 28.06.17.
 */
public interface ServiceIntf {


    Response returnFive();

    Response newEntity1(int intValue, String stringValue);

    Response getStringValueFor(long id);

    Response getIntValueFor(long id);

}
