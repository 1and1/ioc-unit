package com.oneandone.ejbcdiunit.example4;

import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class AsynchronousService implements AsynchronousServiceIntf {

    @Inject
    AsynchronousWrapper asyncService;

    AtomicLong atomicLong = new AtomicLong(0);

    @Override
    public CorrelationId newRemoteEntity1(int intValue, String stringValue) {
        final CorrelationId correlationId = new CorrelationId(atomicLong.incrementAndGet());
        asyncService.newEntity1(correlationId, intValue, stringValue);
        return correlationId;
    }

    @Override
    public CorrelationId getRemoteStringValueFor(long id) {
        final CorrelationId correlationId = new CorrelationId(atomicLong.incrementAndGet());
        asyncService.getStringValueFor(correlationId,id);
        return correlationId;
    }


}
