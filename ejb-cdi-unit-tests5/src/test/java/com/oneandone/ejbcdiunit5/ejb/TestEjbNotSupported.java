package com.oneandone.ejbcdiunit5.ejb;

import com.oneandone.ejbcdiunit.SessionContextFactory;
import com.oneandone.ejbcdiunit.ejbs.SingletonEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessEJB;
import com.oneandone.ejbcdiunit.ejbs.StatelessNotSupportedEJB;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.persistence.SinglePersistenceFactory;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.LoggerGenerator;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ StatelessEJB.class, SingletonEJB.class,
        TestEjb.TestDbPersistenceFactory.class, SessionContextFactory.class, LoggerGenerator.class})
public class TestEjbNotSupported {

    @Inject
    EntityManager em;
    @Inject
    SinglePersistenceFactory persistenceFactory;
    @Inject
    StatelessNotSupportedEJB statelessNotSupportedEJB;

    @Test
    public void testRequiredTraPlusRTException() {
        Assertions.assertThrows(RuntimeException.class, () -> statelessNotSupportedEJB.testRequiredTraPlusRTException());
    }

    @Test
    public void testRequiredTraPlusIOException() throws IOException {
        Assertions.assertThrows(IOException.class, () -> statelessNotSupportedEJB.testRequiredTraPlusIOException());
    }

    @Test
    public void testRequiresNewTraPlusRTException() {
        Assertions.assertThrows(RuntimeException.class, () -> statelessNotSupportedEJB.testRequiresNewTraPlusRTException());
    }

    @Test
    public void testRequiresNewTraPlusIOException() throws IOException {
        Assertions.assertThrows(IOException.class, () -> statelessNotSupportedEJB.testRequiresNewTraPlusIOException());
    }

    @Test
    public void testRequiredIndirectTraPlusRTException() {
        Assertions.assertThrows(RuntimeException.class, () -> statelessNotSupportedEJB.testRequiredIndirectTraPlusRTException());
    }

    @Test
    public void testRequiredIndirectTraPlusIOException() throws IOException {
        Assertions.assertThrows(IOException.class, () -> statelessNotSupportedEJB.testRequiredIndirectTraPlusIOException());
    }

    @Test
    public void testRequiredIndirectTraPlusRTExceptionIndirect() {
        Assertions.assertThrows(RuntimeException.class, () -> statelessNotSupportedEJB.testRequiredIndirectTraPlusRTExceptionIndirect());
    }

    @Test
    public void testRequiredIndirectTraPlusIOExceptionIndirect() throws IOException {
        Assertions.assertThrows(IOException.class, () -> statelessNotSupportedEJB.testRequiredIndirectTraPlusIOExceptionIndirect());
    }

    @Test
    public void persistRequiresNewSetRollbackOnlyBySessionContext() throws IOException {
        statelessNotSupportedEJB.persistRequiresNewSetRollbackOnlyBySessionContext(new TestEntity1());
        Number res = em.createQuery("select count(e) from TestEntity1 e", Number.class).getSingleResult();
        assertThat(res.intValue(), is(0));
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
