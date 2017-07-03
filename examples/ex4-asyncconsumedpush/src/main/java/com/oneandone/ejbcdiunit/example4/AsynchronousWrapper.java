package com.oneandone.ejbcdiunit.example4;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.oneandone.ejbcdiunit.example4.AsynchronousServiceIntf.Callbacks;
import com.oneandone.ejbcdiunit.example4.AsynchronousServiceIntf.CorrelationId;

/**
 * @author aschoerk
 */
@Stateless
public class AsynchronousWrapper {

    @EJB(name = "RemoteServiceIntf")
    RemoteServiceIntf remoteService;

    @EJB(name = "Callback")
    Callbacks callbacks;

    @Asynchronous
    public Future<Integer> returnFive() {
        return new AsyncResult<Integer>(remoteService.returnFive());
    }

    @Asynchronous
    public Future<Long> newEntity1(CorrelationId correlationId, int intValue, String stringValue) {
        final long l = remoteService.newEntity1(intValue, stringValue);
        callbacks.pushId(correlationId, l);
        return new AsyncResult<Long>(l);
    }


    @Asynchronous
    public Future<String> getStringValueFor(CorrelationId correlationId, long id) {
        final String stringValueFor = remoteService.getStringValueFor(id);
        callbacks.pushString(correlationId, stringValueFor);
        return new AsyncResult<String>(stringValueFor);
    }
}
