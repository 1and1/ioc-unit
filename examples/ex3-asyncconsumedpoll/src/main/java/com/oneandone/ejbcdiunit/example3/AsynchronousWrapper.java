package com.oneandone.ejbcdiunit.example3;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

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
