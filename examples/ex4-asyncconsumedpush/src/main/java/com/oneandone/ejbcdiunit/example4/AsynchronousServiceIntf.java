package com.oneandone.ejbcdiunit.example4;

/**
 * Created by aschoerk on 28.06.17.
 */
public interface AsynchronousServiceIntf {

    static class CorrelationId {
        public CorrelationId(long id) {
            this.id = id;
        }

        long id;
    }

    CorrelationId newRemoteEntity1(int intValue, String stringValue);

    CorrelationId getRemoteStringValueFor(long id);

    static interface Callbacks {
        void pushId(CorrelationId correlationId, Long id);

        void pushString(CorrelationId correlationId, String resultString);
    }

}
