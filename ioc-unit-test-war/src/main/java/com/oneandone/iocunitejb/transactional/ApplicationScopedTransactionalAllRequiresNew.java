package com.oneandone.iocunitejb.transactional;

import java.nio.InvalidMarkException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@RequiresNewStereotypeIllegalStateRollsback
@ApplicationScoped
public class ApplicationScopedTransactionalAllRequiresNew {
    @Inject
    EntityManager em;

    public void insertOnly(TestEntity1 e) {
        em.persist(e);
    }

    public void insertRE(TestEntity1 e) {
        em.persist(e);
        throw new RuntimeException("runtimeException");
    }

    public void insertDerivedRE(TestEntity1 e) {
        em.persist(e);
        throw new ArithmeticException("arithmenticException");
    }

    public void insertIllegalState(TestEntity1 e) {
        em.persist(e);
        throw new IllegalStateException("illegalStateException should rollback");
    }

    public void insertDerivedIllegalState(TestEntity1 e) {
        em.persist(e);
        throw new InvalidMarkException();
    }

    public void insertCheckedException(TestEntity1 e) throws CheckedException {
        em.persist(e);
        throw new CheckedException();
    }

    public void insertDerivedCheckedException(TestEntity1 e) throws CheckedException {
        em.persist(e);
        throw new CheckedException() {
            private static final long serialVersionUID = -8316740839334218452L;
        };
    }
}
