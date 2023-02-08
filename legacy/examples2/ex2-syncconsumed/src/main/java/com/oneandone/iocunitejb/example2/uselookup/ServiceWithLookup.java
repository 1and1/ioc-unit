package com.oneandone.iocunitejb.example2.uselookup;

import jakarta.inject.Inject;

import com.oneandone.iocunitejb.example2.ServiceIntf;

/**
 * @author aschoerk
 */
public class ServiceWithLookup implements ServiceIntf {

    @Inject
    Resources resources;

    @Override
    public long newRemoteEntity1(int intValue, String stringValue) {
        return resources.lookupRemoteService().newEntity1(intValue, stringValue);
    }

    @Override
    public String getRemoteStringValueFor(long id) {
        return resources.lookupRemoteService().getStringValueFor(id);
    }
}
