package com.oneandone.iocunitejb.jpa;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunitejb.cdiunit.Pu1Em;
import com.oneandone.iocunitejb.cdiunit.Pu2Em;
import com.oneandone.iocunitejb.resources.Resources;

/**
 * @author aschoerk
 */
@Specializes
public class TomeeResources extends Resources {

    @Produces
    Logger logger = LoggerFactory.getLogger("ejb-cdi-unit-tomee");

    @Produces
    @Pu1Em
    @Pu2Em
    @PersistenceContext(unitName = "test-unit")
    EntityManager entityManager1;

    @PersistenceContext(unitName = "test-unit", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Produces
    public EntityManager produEntityManager() {
        return entityManager;
    }
}
