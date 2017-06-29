package com.oneandone.ejbcdiunit.example6;

import java.io.Serializable;

/**
 * Created by aschoerk on 28.06.17.
 */
public interface AsynchronousServiceIntf {

    static class CorrelationId implements Serializable {
        private static final long serialVersionUID = -7428120337093772044L;

        public CorrelationId(long id) {
            this.id = id;
        }

        long id;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CorrelationId that = (CorrelationId) o;

            return id == that.id;
        }

        @Override
        public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    }

    CorrelationId newRemoteEntity1(int intValue, String stringValue);

    CorrelationId getRemoteStringValueFor(long id);

    static interface Callbacks {
        void pushId(CorrelationId correlationId, Long id);

        void pushString(CorrelationId correlationId, String resultString);
    }

}
