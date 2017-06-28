package com.oneandone.ejbcdiunit.test;

import java.util.concurrent.ExecutionException;

/**
 * Created by aschoerk on 28.06.17.
 */
public interface ServiceIntf {

    static class CorrelationId {
        public CorrelationId(long id) {
            this.id = id;
        }

        long id;
    }

    CorrelationId newRemoteEntity1(int intValue, String stringValue);

    CorrelationId getRemoteStringValueFor(long id);

    Long pollId(CorrelationId correlationId) throws ExecutionException, InterruptedException;

    String pollString(CorrelationId correlationId) throws ExecutionException, InterruptedException;

}