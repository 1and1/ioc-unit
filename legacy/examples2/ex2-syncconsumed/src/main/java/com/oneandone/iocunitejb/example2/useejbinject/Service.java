package com.oneandone.iocunitejb.example2.useejbinject;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import com.oneandone.iocunitejb.example2.RemoteServiceIntf;
import com.oneandone.iocunitejb.example2.ServiceIntf;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class Service implements ServiceIntf {

    @EJB(mappedName = "RemoteServiceIntf")
    RemoteServiceIntf remoteService;

    @Override
    public long newRemoteEntity1(int intValue, String stringValue) {
        return remoteService.newEntity1(intValue, stringValue);
    }

    @Override
    public String getRemoteStringValueFor(long id) {
        return remoteService.getStringValueFor(id);
    }
}
