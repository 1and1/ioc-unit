package com.oneandone.ejbcdiunit.example3;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class Service implements ServiceIntf {

    @Inject
    AsynchService asyncService;

    Map<CorrelationId, Future<?>> futures = new HashMap<>();
    AtomicLong atomicLong = new AtomicLong(0);

    CorrelationId insertFuture(Future<?> furture) {
        final CorrelationId correlationId = new CorrelationId(atomicLong.incrementAndGet());
        futures.put(correlationId, furture);
        return correlationId;
    }

    @Override
    public CorrelationId newRemoteEntity1(int intValue, String stringValue) {
        try {
            return insertFuture(asyncService.newEntity1(intValue, stringValue));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CorrelationId getRemoteStringValueFor(long id) {
        try {
            return insertFuture(asyncService.getStringValueFor(id));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long pollId(CorrelationId correlationId) throws ExecutionException, InterruptedException {
        Future<?> future = getFuture(correlationId);
        return future.isDone() ? (Long)future.get() : null;
    }

    private Future<?> getFuture(ServiceIntf.CorrelationId correlationId) {
        Future<?> future = futures.get(correlationId);
        if (future == null)
            throw new RuntimeException("CorrelationId not found");
        return future;
    }

    @Override
    public String pollString(CorrelationId correlationId) throws ExecutionException, InterruptedException {
        Future<?> future = getFuture(correlationId);
        return future.isDone() ? (String)future.get() : null;
    }
}
