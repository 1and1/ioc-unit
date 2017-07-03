package com.oneandone.ejbcdiunit.example7;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class Service implements ServiceIntf {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Response returnFive() {
        return Response.status(200).entity("5").build();
    }


    @Override
    public Response newEntity1(int intValue, String stringValue) {
        Entity1 entity1 = new Entity1(intValue, stringValue);
        entityManager.persist(entity1);
        long id = entity1.getId().longValue();
        return Response.status(200).entity(Long.toString(entity1.getId())).build();
    }

    @Override
    public Response getStringValueFor(long id) {
        Entity1 entity = entityManager.createQuery("select e from Entity1 e where e.id = :id", Entity1.class)
                .setParameter("id", id)
                .getSingleResult();
        return Response.status(200).entity(entity.getStringValue()).build();
    }

    @Override
    public Response getIntValueFor(long id) {
        Entity1 entity = entityManager.createQuery("select e from Entity1 e where e.id = :id", Entity1.class)
                .setParameter("id", id)
                .getSingleResult();
        return Response.status(200).entity(Integer.toString(entity.getIntValue())).build();
    }
}
