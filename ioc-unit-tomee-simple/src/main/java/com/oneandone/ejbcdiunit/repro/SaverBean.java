package com.oneandone.ejbcdiunit.repro;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * @author aschoerk
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SaverBean {

    @Inject
    private EntityManager entityManager;

    public void saveOneEntity() {
        ReproEntity reproEntity = new ReproEntity("example");
        entityManager.persist(reproEntity);
    }
}
