package com.oneandone.iocunitejb.ejbs;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Resource;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Singleton;

/**
 * @author aschoerk
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MdbEjbInfoSingleton {
    private AtomicInteger numberOfQCalls = new AtomicInteger();

    private AtomicInteger numberOfQCalls2 = new AtomicInteger();

    private AtomicInteger numberOfTCalls = new AtomicInteger();

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

    @Resource
    private EJBContext ejbContext;

    public EJBContext getEjbContext() {
        return ejbContext;
    }
}
