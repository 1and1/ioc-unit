package com.oneandone.iocunitejb.example2;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Default
public class RemoteServiceSimulator implements RemoteServiceIntf {
    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public int returnFive() {
        return 5;
    }

    @Override
    public long newEntity1(int intValue, String stringValue) {
        return atomicInteger.incrementAndGet();
    }

    @Override
    public String getStringValueFor(long id) {
        return "string: " + (id-1L);
    }
}
