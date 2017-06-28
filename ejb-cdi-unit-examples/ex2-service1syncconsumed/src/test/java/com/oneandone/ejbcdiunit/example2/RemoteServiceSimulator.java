package com.oneandone.ejbcdiunit.example2;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import com.oneandone.ejbcdiunit.cdiunit.EjbQualifier;
import com.oneandone.ejbcdiunit.example2.RemoteServiceIntf;

/**
 * @author aschoerk
 */
@ApplicationScoped
@EjbQualifier
@Default
public class RemoteServiceSimulator implements RemoteServiceIntf {
    @Override
    public int returnFive() {
        return 5;
    }

    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public long newEntity1(int intValue, String stringValue) {
        return atomicInteger.incrementAndGet();
    }

    @Override
    public String getStringValueFor(long id) {
        return "string: " + (id-1L);
    }
}
