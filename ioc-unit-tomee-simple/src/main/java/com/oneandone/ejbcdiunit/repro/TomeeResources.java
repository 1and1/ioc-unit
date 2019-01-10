package com.oneandone.ejbcdiunit.repro;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author aschoerk
 */
public class TomeeResources {

    @Produces
    Logger logger = LoggerFactory.getLogger("ejb-cdi-unit-tomee");

    @PersistenceContext
    EntityManager entityManager;

    @Produces
    EntityManager entityManager() {
        return entityManager;
    }

}
