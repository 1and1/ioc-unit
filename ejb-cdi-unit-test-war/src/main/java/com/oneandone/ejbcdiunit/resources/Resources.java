package com.oneandone.ejbcdiunit.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.cdiunit.Pu1Em;
import com.oneandone.ejbcdiunit.cdiunit.Pu2Em;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class Resources {

    @Produces
    Logger logger = LoggerFactory.getLogger("ejb-cdi-unit-test-war");

    @Produces
    @Pu1Em
    @Default
    @PersistenceContext(unitName = "EjbTestPU")
    EntityManager entityManager1;

    @Produces
    @Pu2Em
    @PersistenceContext(unitName = "EjbTestPUOperating")
    EntityManager entityManager2;



}
