package com.oneandone.iocunit.jta.narayana;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class PersistenceFactory {
    EntityManagerFactory entityManagerFactory;

    @Produces
    EntityManager createEntityManager(InjectionPoint injectionPoint)  {
        injectionPoint.getQualifiers();
        if (entityManagerFactory == null)
            entityManagerFactory = Persistence.createEntityManagerFactory("jtspgdb");
        return entityManagerFactory.createEntityManager();
    }

}
