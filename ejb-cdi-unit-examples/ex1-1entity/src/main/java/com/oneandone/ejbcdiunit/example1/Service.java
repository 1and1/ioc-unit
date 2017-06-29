package com.oneandone.ejbcdiunit.example1;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class Service implements ServiceIntf {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public int returnFive() {
        return 5;
    }

    @Override
    public long newEntity1(int intValue, String stringValue) {
        Entity1 entity1 = new Entity1(intValue, stringValue);
        entityManager.persist(entity1);
        return entity1.getId().longValue();
    }

    @Override
    public String getStringValueFor(long id) {
        return entityManager.createQuery("select e.stringValue from Entity1 e where e.id = :id", String.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
