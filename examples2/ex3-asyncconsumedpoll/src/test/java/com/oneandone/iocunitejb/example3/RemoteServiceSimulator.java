package com.oneandone.iocunitejb.example3;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
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
        return "string: " + (id - 1L);
    }

    public AtomicInteger getAtomicInteger() {
        return atomicInteger;
    }
}
