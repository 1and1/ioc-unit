package com.oneandone.iocunitejb.ejbs;

import java.io.IOException;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class StatelessNotSupportedEJB {

    @Inject
    StatelessEJB statelessEJB;

    public void testRequiredTraPlusRTException() {
        statelessEJB.persistRequiredAndRTException(new TestEntity1());
    }

    public void testRequiredTraPlusIOException() throws IOException {
        statelessEJB.persistRequiredAndIOException(new TestEntity1());
    }

    public void testRequiresNewTraPlusRTException() {
        statelessEJB.persistRequiresNewAndRTException(new TestEntity1());
    }

    public void testRequiresNewTraPlusIOException() throws IOException {
        statelessEJB.persistRequiresNewAndIOException(new TestEntity1());
    }

    public void testRequiredIndirectTraPlusRTException() {
        statelessEJB.persistRequiredIndirectAndRTExceptionIndirect(new TestEntity1());
    }

    public void testRequiredIndirectTraPlusIOException() throws IOException {
        statelessEJB.persistRequiredIndirectAndIOExceptionIndirect(new TestEntity1());
    }

    public void testRequiredIndirectTraPlusRTExceptionIndirect() {
        statelessEJB.persistRequiresNewIndirectAndRTExceptionIndirect(new TestEntity1());
    }

    public void testRequiredIndirectTraPlusIOExceptionIndirect() throws IOException {
        statelessEJB.persistRequiresNewIndirectAndIOExceptionIndirect(new TestEntity1());
    }

    public boolean persistRequiresNewSetRollbackOnlyBySessionContext(TestEntity1 testEntity1) throws IOException {
        return statelessEJB.persistRequiresNewSetRollbackOnlyBySessionContext(testEntity1);
    }

    public boolean persistRequiresNewGetRollbackOnlyBySessionContext(TestEntity1 testEntity1) throws IOException {
        return statelessEJB.persistRequiresNewGetRollbackOnlyBySessionContext(testEntity1);
    }
}
