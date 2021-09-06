package com.oneandone.cdi.discoveryrunner;

import javax.enterprise.inject.Produces;

/**
 * @author aschoerk
 */

public abstract class ProducerTestBase {

    public static class InnerProduced {
        int i;

        public InnerProduced(final int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }
    }

    @Produces
    InnerProduced innerProduced = new InnerProduced(1);

}
