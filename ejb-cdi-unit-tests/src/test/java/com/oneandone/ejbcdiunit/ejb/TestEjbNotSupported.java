package com.oneandone.ejbcdiunit.ejb;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.SessionContextFactory;
import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessNotSupportedEJB;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.helpers.LoggerGenerator;
import com.oneandone.ejbcdiunit.persistence.SinglePersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ StatelessEJB.class, SingletonEJB.class,
        TestEjb.TestDbPersistenceFactory.class, SessionContextFactory.class, LoggerGenerator.class})
public class TestEjbNotSupported {

    @Inject
    EntityManager em;
    @Inject
    SinglePersistenceFactory persistenceFactory;
    @Inject
    StatelessNotSupportedEJB statelessNotSupportedEJB;

    @Test(expected = RuntimeException.class)
    public void testRequiredTraPlusRTException() {
        statelessNotSupportedEJB.testRequiredTraPlusRTException();
    }

    @Test(expected = IOException.class)
    public void testRequiredTraPlusIOException() throws IOException {
        statelessNotSupportedEJB.testRequiredTraPlusIOException();
    }

    @Test(expected = RuntimeException.class)
    public void testRequiresNewTraPlusRTException() {
        statelessNotSupportedEJB.testRequiresNewTraPlusRTException();
    }

    @Test(expected = IOException.class)
    public void testRequiresNewTraPlusIOException() throws IOException {
        statelessNotSupportedEJB.testRequiresNewTraPlusIOException();
    }

    @Test(expected = RuntimeException.class)
    public void testRequiredIndirectTraPlusRTException() {
        statelessNotSupportedEJB.testRequiredIndirectTraPlusRTException();
    }

    @Test(expected = IOException.class)
    public void testRequiredIndirectTraPlusIOException() throws IOException {
        statelessNotSupportedEJB.testRequiredIndirectTraPlusIOException();
    }

    @Test(expected = RuntimeException.class)
    public void testRequiredIndirectTraPlusRTExceptionIndirect() {
        statelessNotSupportedEJB.testRequiredIndirectTraPlusRTExceptionIndirect();
    }

    @Test(expected = IOException.class)
    public void testRequiredIndirectTraPlusIOExceptionIndirect() throws IOException {
        statelessNotSupportedEJB.testRequiredIndirectTraPlusIOExceptionIndirect();
    }

    @Test
    public void persistRequiresNewSetRollbackOnlyBySessionContext() throws IOException {
        statelessNotSupportedEJB.persistRequiresNewSetRollbackOnlyBySessionContext(new TestEntity1());
        Number res = em.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        Assert.assertThat(res.intValue(), is(0));
    }

    @Test
    public void persistRequiresNewGetRollbackOnlyBySessionContext() throws IOException {
        assertThat(statelessNotSupportedEJB.persistRequiresNewGetRollbackOnlyBySessionContext(new TestEntity1()), is(false));
    }

    @ApplicationScoped
    public static class TestDbPersistenceFactory extends SinglePersistenceFactory {

        @Produces
        @Override
        public EntityManager newEm() {
            return produceEntityManager();
        }
    }
}
