package com.oneandone.iocunitejb.example3;

import java.util.concurrent.Future;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

/**
 * @author aschoerk
 */
@Stateless
public class AsynchronousWrapper {

    @EJB(name = "RemoteServiceIntf")
    RemoteServiceIntf remoteService;

    @Asynchronous
    public Future<Integer> returnFive() {
        return new AsyncResult<Integer>(remoteService.returnFive());
    }

    @Asynchronous
    public Future<Long> newEntity1(int intValue, String stringValue) {
        return new AsyncResult<Long>(remoteService.newEntity1(intValue, stringValue));
    }

    @Asynchronous
    public Future<String> getStringValueFor(long id) {
        return new AsyncResult<String>(remoteService.getStringValueFor(id));
    }
}
