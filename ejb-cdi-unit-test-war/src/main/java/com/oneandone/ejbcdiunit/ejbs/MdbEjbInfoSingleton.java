package com.oneandone.ejbcdiunit.ejbs;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

/**
 * @author aschoerk
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MdbEjbInfoSingleton {
    private static AtomicInteger numberOfQCalls = new AtomicInteger();

    private static AtomicInteger numberOfQCalls2 = new AtomicInteger();

    private static AtomicInteger numberOfTCalls = new AtomicInteger();

    public int getNumberOfTCalls() {
        return numberOfTCalls.get();
    }

    public int getNumberOfQCalls() {
        return numberOfQCalls.get();
    }
    public int getNumberOfQCalls2() {
        return numberOfQCalls2.get();
    }

    public void incrementNumberOfQCalls() {
        numberOfQCalls.incrementAndGet();
    }

    public void incrementNumberOfQCalls2() {
        numberOfQCalls2.incrementAndGet();
    }

    public void incrementNumberOfTCalls() {
        numberOfTCalls.incrementAndGet();
    }
}
