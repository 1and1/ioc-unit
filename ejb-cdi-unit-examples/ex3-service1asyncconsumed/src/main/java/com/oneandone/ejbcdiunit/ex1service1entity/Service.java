package com.oneandone.ejbcdiunit.ex1service1entity;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class Service implements ServiceIntf {

    @Inject
    AsynchService asyncService;

    @Override
    public long newRemoteEntity1(int intValue, String stringValue) {
        try {
            return asyncService.newEntity1(intValue, stringValue).get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRemoteStringValueFor(long id) {
        try {
            return asyncService.getStringValueFor(id).get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
